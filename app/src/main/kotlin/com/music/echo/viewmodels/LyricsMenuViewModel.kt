/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.LyricsEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.lyrics.LyricsHelper
import echo.music.iad1tya.lyrics.LyricsResult
import echo.music.iad1tya.lyrics.canReplaceLyricsAfterRefresh
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.utils.NetworkConnectivityObserver
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LyricsMenuViewModel
@Inject
constructor(
  private val lyricsHelper: LyricsHelper,
  val database: MusicDatabase,
  private val networkConnectivity: NetworkConnectivityObserver,
) : ViewModel() {
  private var job: Job? = null
  private var refreshJob: Job? = null
  val results = MutableStateFlow(emptyList<LyricsResult>())
  val isLoading = MutableStateFlow(false)

  private val _isNetworkAvailable = MutableStateFlow(false)
  val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

  private val _currentSong = mutableStateOf<Song?>(null)
  val currentSong: State<Song?> = _currentSong

  init {
    viewModelScope.launch {
      networkConnectivity.networkStatus.collect { isConnected ->
        _isNetworkAvailable.value = isConnected
      }
    }

    _isNetworkAvailable.value =
      try {
        networkConnectivity.isCurrentlyConnected()
      } catch (e: Exception) {
        true
      }
  }

  fun setCurrentSong(song: Song) {
    _currentSong.value = song
  }

  fun search(
    mediaId: String,
    title: String,
    artist: String,
    duration: Int,
    album: String? = null,
  ) {
    isLoading.value = true
    results.value = emptyList()
    job?.cancel()
    job =
      viewModelScope.launch(Dispatchers.IO) {
        lyricsHelper.getAllLyrics(mediaId, title, artist, duration, album) { result ->
          results.update { it + result }
        }
        isLoading.value = false
      }
  }

  fun cancelSearch() {
    job?.cancel()
    job = null
  }

  fun refetchLyrics(
    mediaMetadata: MediaMetadata,
    lyricsEntity: LyricsEntity?,
  ) {
    refreshJob?.cancel()
    refreshJob =
      viewModelScope.launch(Dispatchers.IO) {
        try {
          // Fetch before entering the database executor/transaction, retaining text during
          // failures.
          val refreshed = lyricsHelper.getLyrics(mediaMetadata, forceRefresh = true)
          database.withTransaction {
            val current = lyrics(mediaMetadata.id).first()
            if (
              canReplaceLyricsAfterRefresh(lyricsEntity?.lyrics, current?.lyrics, refreshed.lyrics)
            ) {
              upsert(LyricsEntity(mediaMetadata.id, refreshed.lyrics!!, refreshed.providerName))
            }
          }
        } catch (cancelled: CancellationException) {
          throw cancelled
        } catch (error: Exception) {
          timber.log.Timber.w(error, "Could not refresh lyrics; existing lyrics retained")
        }
      }
  }
}
