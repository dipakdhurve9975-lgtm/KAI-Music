// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.updates

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readAvailable
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

internal class KaiUpdateFailure(val reason: Reason) : Exception(reason.name) {
  enum class Reason {
    NETWORK,
    RATE_LIMIT,
    SERVER,
    METADATA,
    DOWNLOAD,
    VERIFY,
    INSTALL,
  }
}

/** Injectable JSON reader keeps release selection and failure tests independent of the network. */
internal class KaiReleaseSource(private val read: suspend (String, Int) -> String) {
  private val json = Json { ignoreUnknownKeys = true }

  suspend fun findUpdate(installed: KaiInstalledIdentity, preview: Boolean): KaiUpdateOffer? {
    val releaseJson = read(KAI_RELEASE_API, 2 * 1024 * 1024)
    val releases =
      try {
        json.decodeFromString<List<KaiRelease>>(releaseJson)
      } catch (invalid: kotlinx.serialization.SerializationException) {
        throw KaiUpdateFailure(KaiUpdateFailure.Reason.METADATA)
      }
    val offers = mutableListOf<KaiUpdateOffer>()
    for (release in releases.filter { approvedRelease(it, preview) }) {
      val asset = release.assets.singleOrNull { it.name == "release-metadata.json" } ?: continue
      if (!trustedAsset(release, asset) || asset.size > 32 * 1024) continue
      val manifest =
        try {
          json.decodeFromString<KaiUpdateManifest>(read(asset.url, 32 * 1024))
        } catch (cancelled: CancellationException) {
          throw cancelled
        } catch (invalid: kotlinx.serialization.SerializationException) {
          throw KaiUpdateFailure(KaiUpdateFailure.Reason.METADATA)
        }
      validateOffer(release, manifest, installed, preview)?.let(offers::add)
    }
    return offers.maxByOrNull { it.manifest.versionCode }
  }
}

internal class KaiUpdateRepository(
  private val clientFactory: () -> HttpClient = {
    HttpClient(OkHttp) {
      followRedirects = false
      engine {
        config {
          followRedirects(false)
          followSslRedirects(false)
        }
      }
      install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
        requestTimeoutMillis = 10 * 60_000
      }
    }
  }
) {
  private val http by lazy(clientFactory)
  val source = KaiReleaseSource(::readJson)

  private suspend fun <T> withResponse(initial: String, consume: suspend (HttpResponse) -> T): T {
    // Initial URLs are constructed by this application or exact-match validated repository assets.
    var url = initial
    repeat(4) {
      val result =
        http
          .prepareGet(url) {
            header(HttpHeaders.UserAgent, "KAI-Music-updater")
            header(HttpHeaders.Accept, "application/vnd.github+json")
          }
          .execute { response ->
            when (response.status.value) {
              200 -> UpdateHttpResult.Value(consume(response))
              301,
              302,
              303,
              307,
              308 -> {
                val location = response.headers[HttpHeaders.Location]
                if (location == null || !approvedDownloadRedirect(location))
                  throw KaiUpdateFailure(KaiUpdateFailure.Reason.DOWNLOAD)
                UpdateHttpResult.Redirect(location)
              }
              403,
              429 -> {
                throw KaiUpdateFailure(KaiUpdateFailure.Reason.RATE_LIMIT)
              }
              else -> {
                throw KaiUpdateFailure(KaiUpdateFailure.Reason.SERVER)
              }
            }
          }
      when (result) {
        is UpdateHttpResult.Value -> return result.value
        is UpdateHttpResult.Redirect -> url = result.url
      }
    }
    throw KaiUpdateFailure(KaiUpdateFailure.Reason.DOWNLOAD)
  }

  private suspend fun readJson(initial: String, limit: Int): String {
    return withTimeout(30_000L) {
      withResponse(initial) { response ->
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        val channel = response.bodyAsChannel()
        while (true) {
          currentCoroutineContext().ensureActive()
          val count = channel.readAvailable(buffer, 0, buffer.size)
          if (count < 0) break
          if (output.size() + count > limit)
            throw KaiUpdateFailure(KaiUpdateFailure.Reason.METADATA)
          output.write(buffer, 0, count)
        }
        output.toString(Charsets.UTF_8.name())
      }
    }
  }

  suspend fun download(offer: KaiUpdateOffer, file: File, progress: (Long) -> Unit) {
    require(trustedAsset(offer.release, offer.apk))
    val digest = MessageDigest.getInstance("SHA-256")
    withResponse(offer.apk.url) { response ->
      response.headers[HttpHeaders.ContentLength]?.toLongOrNull()?.let {
        if (it != offer.apk.size) throw KaiUpdateFailure(KaiUpdateFailure.Reason.VERIFY)
      }
      file.outputStream().use { output ->
        val channel = response.bodyAsChannel()
        val buffer = ByteArray(64 * 1024)
        var received = 0L
        var lastProgress = 0L
        while (true) {
          currentCoroutineContext().ensureActive()
          val count = channel.readAvailable(buffer, 0, buffer.size)
          if (count < 0) break
          received += count
          if (received > offer.apk.size || received > MAX_APK_BYTES)
            throw KaiUpdateFailure(KaiUpdateFailure.Reason.VERIFY)
          output.write(buffer, 0, count)
          digest.update(buffer, 0, count)
          if (received - lastProgress >= 256 * 1024 || received == offer.apk.size) {
            progress(received)
            lastProgress = received
          }
        }
        if (received != offer.apk.size) throw KaiUpdateFailure(KaiUpdateFailure.Reason.VERIFY)
      }
    }
    if (digest.digest().toHex() != offer.manifest.sha256)
      throw KaiUpdateFailure(KaiUpdateFailure.Reason.VERIFY)
  }
}

private sealed interface UpdateHttpResult<out T> {
  data class Value<T>(val value: T) : UpdateHttpResult<T>

  data class Redirect(val url: String) : UpdateHttpResult<Nothing>
}

internal fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
