// Modified for KAI Music on 2026-10-29; GPL-3.0.
package echo.music.iad1tya.updates

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class KaiUpdateDownloadTest {
  private val payload = ByteArray(900_000) { (it % 251).toByte() }

  private fun offer(): KaiUpdateOffer {
    val tag = "kai-preview-1.4.9"
    val asset =
      KaiReleaseAsset(
        "KAI-universal-gms-preview.apk",
        payload.size.toLong(),
        "$KAI_RELEASES/download/$tag/KAI-universal-gms-preview.apk",
      )
    return KaiUpdateOffer(
      KaiRelease(tag, prerelease = true, url = "$KAI_RELEASES/tag/$tag", assets = listOf(asset)),
      KaiUpdateManifest(
        1,
        "echo.music.iad1tya.debug",
        170,
        "1.4.9",
        "preview",
        "universalGms",
        26,
        listOf("arm64-v8a"),
        asset.name,
        MessageDigest.getInstance("SHA-256").digest(payload).toHex(),
        "a".repeat(64),
        "b".repeat(40),
      ),
      asset,
    )
  }

  private fun temporary() =
    File.createTempFile("kai-update-test-", ".partial").apply { deleteOnExit() }

  @Test
  fun streamsOneRequestWithProgressAndExactChecksum() = runTest {
    val engine = MockEngine {
      respond(
        ByteReadChannel(payload),
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentLength, payload.size.toString()),
      )
    }
    val client = HttpClient(engine) { followRedirects = false }
    val repository = KaiUpdateRepository { client }
    val progress = mutableListOf<Long>()
    val file = temporary()
    try {
      repository.download(offer(), file, progress::add)
      assertArrayEquals(payload, file.readBytes())
      assertEquals(payload.size.toLong(), progress.last())
      assertEquals(progress.sorted(), progress)
      assertEquals(1, engine.requestHistory.size)
    } finally {
      file.delete()
      client.close()
    }
  }

  @Test
  fun checksumMismatchRejectsDownloadedBytes() = runTest {
    val engine = MockEngine { respond(ByteReadChannel(payload)) }
    val client = HttpClient(engine) { followRedirects = false }
    val file = temporary()
    val original = offer()
    try {
      KaiUpdateRepository { client }
        .download(
          original.copy(manifest = original.manifest.copy(sha256 = "c".repeat(64))),
          file,
        ) {}
      fail("Checksum mismatch must fail")
    } catch (expected: KaiUpdateFailure) {
      assertEquals(KaiUpdateFailure.Reason.VERIFY, expected.reason)
    } finally {
      file.delete()
      client.close()
    }
  }

  @Test
  fun truncatedAndOversizedDownloadsAreRejected() = runTest {
    for (bytes in listOf(payload.copyOf(100), payload + byteArrayOf(1))) {
      val engine = MockEngine { respond(ByteReadChannel(bytes)) }
      val client = HttpClient(engine) { followRedirects = false }
      val file = temporary()
      try {
        KaiUpdateRepository { client }.download(offer(), file) {}
        fail("Wrong length must fail")
      } catch (expected: KaiUpdateFailure) {
        assertEquals(KaiUpdateFailure.Reason.VERIFY, expected.reason)
      } finally {
        file.delete()
        client.close()
      }
    }
  }

  @Test
  fun downloadCancellationPropagates() = runTest {
    val engine = MockEngine { respond(ByteReadChannel(payload)) }
    val client = HttpClient(engine) { followRedirects = false }
    val file = temporary()
    try {
      KaiUpdateRepository { client }
        .download(offer(), file) { throw CancellationException("User cancelled") }
      fail("Cancellation must propagate")
    } catch (_: CancellationException) {} finally {
      file.delete()
      client.close()
    }
  }

  @Test
  fun unapprovedRedirectIsNeverRequested() = runTest {
    val engine = MockEngine {
      respond(
        "",
        HttpStatusCode.Found,
        headersOf(HttpHeaders.Location, "https://untrusted.example/update.apk"),
      )
    }
    val client = HttpClient(engine) { followRedirects = false }
    val file = temporary()
    try {
      KaiUpdateRepository { client }.download(offer(), file) {}
      fail("Untrusted redirect must fail")
    } catch (expected: KaiUpdateFailure) {
      assertEquals(KaiUpdateFailure.Reason.DOWNLOAD, expected.reason)
    } finally {
      assertEquals(1, engine.requestHistory.size)
      file.delete()
      client.close()
    }
  }

  @Test
  fun githubAssetRedirectIsStreamedWithoutFollowingArbitraryHosts() = runTest {
    val engine = MockEngine { request ->
      if (request.url.host == "github.com")
        respond(
          "",
          HttpStatusCode.Found,
          headersOf(HttpHeaders.Location, "https://release-assets.githubusercontent.com/test/file"),
        )
      else {
        assertEquals("release-assets.githubusercontent.com", request.url.host)
        respond(ByteReadChannel(payload))
      }
    }
    val client = HttpClient(engine) { followRedirects = false }
    val file = temporary()
    try {
      KaiUpdateRepository { client }.download(offer(), file) {}
      assertEquals(2, engine.requestHistory.size)
      assertArrayEquals(payload, file.readBytes())
    } finally {
      file.delete()
      client.close()
    }
  }

  @Test
  fun serverFailuresAndRateLimitsAreVisible() = runTest {
    for ((status, reason) in
      listOf(
        HttpStatusCode.NotFound to KaiUpdateFailure.Reason.SERVER,
        HttpStatusCode.TooManyRequests to KaiUpdateFailure.Reason.RATE_LIMIT,
      )) {
      val engine = MockEngine { respond("", status) }
      val client = HttpClient(engine) { followRedirects = false }
      val file = temporary()
      try {
        KaiUpdateRepository { client }.download(offer(), file) {}
        fail("HTTP failure must fail")
      } catch (expected: KaiUpdateFailure) {
        assertEquals(reason, expected.reason)
      } finally {
        file.delete()
        client.close()
      }
    }
  }
}
