package echo.music.iad1tya.lyrics

import android.content.Context
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.FetchFasterLyricsKey
import echo.music.iad1tya.constants.LyricsProviderOrderKey
import echo.music.iad1tya.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.playback.LyricsWithProvider
import echo.music.iad1tya.utils.NetworkConnectivityObserver
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import echo.music.iad1tya.utils.reportException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class LyricsHelper
@Inject
constructor(
  @ApplicationContext private val context: Context,
  private val networkConnectivity: NetworkConnectivityObserver,
) {

  private suspend fun resolveLyricsProviders(): List<LyricsProvider> {
    val order = context.dataStore[LyricsProviderOrderKey].orEmpty()
    return LyricsProviderRegistry.getOrderedProviders(order).filter { it.isEnabled(context) }
  }

  private val cache = LruCache<String, List<LyricsResult>>(MAX_CACHE_SIZE)
  private val timingUpgradeAttempts = LruCache<String, Boolean>(64)
  private val inFlightRequests = ConcurrentHashMap<String, Deferred<LyricsWithProvider>>()
  private val fetchScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private var currentLyricsJob: Job? = null

  suspend fun getLyrics(mediaMetadata: MediaMetadata): LyricsWithProvider {

    val cached = cache.get(mediaMetadata.id)?.firstOrNull()
    if (cached != null) {
      return LyricsWithProvider(cached.lyrics, cached.providerName)
    }

    val isNetworkAvailable =
      try {
        networkConnectivity.isCurrentlyConnected()
      } catch (e: Exception) {
        true
      }

    if (!isNetworkAvailable) {
      return LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
    }

    // Shared fetches live in fetchScope, not the first caller's scope, so one caller being
    // cancelled (e.g. collectLatest on a song switch) can't fail the others awaiting it.
    val created =
      fetchScope.async(start = CoroutineStart.LAZY) { fetchFirstSyncedLyrics(mediaMetadata) }
    val existing = inFlightRequests.putIfAbsent(mediaMetadata.id, created)
    val shared =
      existing
        ?: created.also {
          it.invokeOnCompletion { inFlightRequests.remove(mediaMetadata.id, created) }
          it.start()
        }
    if (existing != null) created.cancel()
    return shared.await()
  }

  /** One bounded attempt per song/session; never replace readable lyrics with a failed result. */
  suspend fun getTimedUpgrade(metadata: MediaMetadata): LyricsWithProvider? {
    cache.get(metadata.id)?.firstOrNull()?.let {
      if (LyricsUtils.hasSyncedLyrics(it.lyrics))
        return LyricsWithProvider(it.lyrics, it.providerName)
    }
    if (!networkConnectivity.isCurrentlyConnected()) return null
    synchronized(timingUpgradeAttempts) {
      if (timingUpgradeAttempts.get(metadata.id) == true) return null
      timingUpgradeAttempts.put(metadata.id, true)
    }
    cache.remove(metadata.id)
    return getLyrics(metadata).takeIf { it.lyrics?.let(LyricsUtils::hasSyncedLyrics) == true }
  }

  private suspend fun fetchFirstSyncedLyrics(mediaMetadata: MediaMetadata): LyricsWithProvider {
    val providers = resolveLyricsProviders()
    if (providers.isEmpty()) return LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")

    return coroutineScope {
      val channel = Channel<Pair<Int, LyricsWithProvider?>>(providers.size)
      providers.forEachIndexed { index, provider ->
        launch {
          val lyrics = fetchLyricsFromProvider(provider, mediaMetadata)
          val result =
            lyrics
              ?.takeIf { it != LYRICS_NOT_FOUND && it.isNotBlank() }
              ?.let { LyricsWithProvider(it, provider.name) }
          channel.send(index to result)
        }
      }
      val completed = BooleanArray(providers.size)
      val results = arrayOfNulls<LyricsWithProvider>(providers.size)
      val timed = mutableSetOf<Int>()
      val fastest = context.dataStore[FetchFasterLyricsKey] ?: false
      repeat(providers.size) {
        val (index, result) = channel.receive()
        completed[index] = true
        results[index] = result
        if (result?.lyrics?.let(LyricsUtils::hasSyncedLyrics) == true) timed.add(index)
        val ready = readyTimedLyricsProvider(completed, timed, fastest)
        if (ready != null) {
          val best = results[ready]!!
          coroutineContext.cancelChildren()
          cache.put(
            mediaMetadata.id,
            listOf(LyricsResult(best.providerName, best.lyrics.orEmpty())),
          )
          return@coroutineScope best
        }
      }
      val best =
        results.firstOrNull { it != null } ?: LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
      if (best.lyrics != null && best.lyrics != LYRICS_NOT_FOUND)
        cache.put(mediaMetadata.id, listOf(LyricsResult(best.providerName, best.lyrics.orEmpty())))
      best
    }
  }

  private suspend fun fetchLyricsFromProvider(
    provider: LyricsProvider,
    mediaMetadata: MediaMetadata,
  ): String? =
    withTimeoutOrNull(PROVIDER_TIMEOUT_MS) {
      try {
        provider
          .getLyrics(
            mediaMetadata.id,
            mediaMetadata.title,
            mediaMetadata.artists.joinToString { it.name },
            mediaMetadata.duration,
            mediaMetadata.album?.title,
          )
          .getOrElse {
            if (it is CancellationException) throw it
            reportException(it)
            null
          }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        reportException(e)
        null
      }
    }

  suspend fun getAllLyrics(
    mediaId: String,
    songTitle: String,
    songArtists: String,
    duration: Int,
    album: String? = null,
    callback: (LyricsResult) -> Unit,
  ) {
    currentLyricsJob?.cancel()

    val cacheKey =
      listOf(mediaId, duration.toString(), songTitle, songArtists, album.orEmpty())
        .joinToString("\u0000")
    cache.get(cacheKey)?.let { results ->
      results.forEach { callback(it) }
      return
    }

    val isNetworkAvailable =
      try {
        networkConnectivity.isCurrentlyConnected()
      } catch (e: Exception) {
        true
      }

    if (!isNetworkAvailable) {
      return
    }

    val allResult = java.util.concurrent.CopyOnWriteArrayList<LyricsResult>()
    val providers = resolveLyricsProviders()
    currentLyricsJob =
      CoroutineScope(SupervisorJob()).launch {
        val jobs = providers.mapNotNull { provider ->
          if (provider.isEnabled(context)) {
            launch {
              try {
                provider.getAllLyrics(mediaId, songTitle, songArtists, duration, album) { lyrics ->
                  val result = LyricsResult(provider.name, lyrics)
                  allResult += result
                  callback(result)
                }
              } catch (e: Exception) {
                reportException(e)
              }
            }
          } else null
        }
        jobs.forEach { it.join() }
        cache.put(cacheKey, allResult.toList())
      }

    currentLyricsJob?.join()
  }

  fun cancelCurrentLyricsJob() {
    currentLyricsJob?.cancel()
    currentLyricsJob = null
  }

  companion object {
    private const val MAX_CACHE_SIZE = 20
    private const val PROVIDER_TIMEOUT_MS = 5_000L
  }
}

data class LyricsResult(
  val providerName: String,
  val lyrics: String,
)

/** Wait only for outstanding higher-priority providers; there is no artificial grace timer. */
internal fun readyTimedLyricsProvider(
  completed: BooleanArray,
  timed: Set<Int>,
  fastest: Boolean,
): Int? {
  val best = timed.minOrNull() ?: return null
  return best.takeIf { fastest || (0 until best).all { completed[it] } }
}
