// Modified for KAI Music on 2026-10-29; GPL-3.0.
package echo.music.iad1tya.updates

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class KaiUpdatePolicyTest {
  private val signer = "a".repeat(64)
  private val installed =
    KaiInstalledIdentity("echo.music.iad1tya.debug", 169, 36, listOf("arm64-v8a"), setOf(signer))
  private val manifest =
    KaiUpdateManifest(
      1,
      installed.applicationId,
      170,
      "1.4.9",
      "preview",
      "universalGms",
      26,
      listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"),
      "KAI-universal-gms-preview.apk",
      "b".repeat(64),
      signer,
      "c".repeat(40),
    )

  private fun release(version: String = "1.4.9", metadata: Boolean = true): KaiRelease {
    val tag = "kai-preview-$version"
    val names = listOfNotNull(manifest.apkFileName, "release-metadata.json".takeIf { metadata })
    return KaiRelease(
      tag,
      prerelease = true,
      url = "$KAI_RELEASES/tag/$tag",
      body = "Actual release notes",
      assets = names.map { KaiReleaseAsset(it, 100, "$KAI_RELEASES/download/$tag/$it") },
    )
  }

  private fun valid() = validateOffer(release(), manifest, installed, true)!!

  @Test
  fun numericVersionCodeIsPrimary() {
    assertNotNull(validateOffer(release(), manifest, installed, true))
    assertNull(validateOffer(release(), manifest.copy(versionCode = 169), installed, true))
    assertNull(validateOffer(release(), manifest.copy(versionCode = 168), installed, true))
  }

  @Test
  fun previewNeedsExplicitOptInAndDraftsNeverAppear() {
    assertNull(validateOffer(release(), manifest, installed, false))
    assertFalse(approvedRelease(release().copy(draft = true), true))
    assertFalse(approvedRelease(release().copy(prerelease = false), false))
  }

  @Test
  fun stableChannelAcceptsOnlyMatchingStableMetadataAndPackage() {
    val stable =
      release()
        .copy(
          tag = "kai-v1.4.9",
          prerelease = false,
          url = "$KAI_RELEASES/tag/kai-v1.4.9",
          assets =
            listOf(
              KaiReleaseAsset(
                "KAI-latest.apk",
                100,
                "$KAI_RELEASES/download/kai-v1.4.9/KAI-latest.apk",
              )
            ),
        )
    assertNotNull(
      validateOffer(
        stable,
        manifest.copy(channel = "stable", apkFileName = "KAI-latest.apk"),
        installed,
        false,
      )
    )
    assertNull(
      validateOffer(
        stable,
        manifest.copy(
          channel = "stable",
          applicationId = "echo.music.iad1tya",
          apkFileName = "KAI-latest.apk",
        ),
        installed,
        false,
      )
    )
  }

  @Test
  fun metadataMustDescribeAnExactTrustedCompatibleAsset() {
    assertNull(validateOffer(release(), manifest.copy(variant = "arm64Gms"), installed, true))
    assertNull(validateOffer(release(), manifest.copy(minSdk = 99), installed, true))
    assertNull(validateOffer(release(), manifest.copy(abis = listOf("x86")), installed, true))
    assertNull(validateOffer(release(), manifest.copy(sha256 = "fake"), installed, true))
    assertNull(
      validateOffer(release(), manifest.copy(signerSha256 = "d".repeat(64)), installed, true)
    )
    assertNull(validateOffer(release(), manifest.copy(schema = 2), installed, true))
    assertNull(validateOffer(release(), manifest.copy(versionName = "1.4.10"), installed, true))
  }

  @Test
  fun upstreamAndLookalikeUrlsAreRejected() {
    val r = release()
    assertFalse(
      approvedRelease(
        r.copy(url = "https://github.com/EchoMusicApp/Echo-Music/releases/tag/${r.tag}"),
        true,
      )
    )
    for (bad in
      listOf(
        "http://github.com/$KAI_REPO",
        "https://github.com.evil.test/$KAI_REPO",
        "https://evil.test/update.apk",
      )) {
      assertFalse(trustedAsset(r, r.assets.first().copy(url = bad)))
    }
    assertFalse(trustedAsset(r, r.assets.first().copy(size = 0)))
    assertFalse(trustedAsset(r, r.assets.first().copy(name = "../update.apk")))
  }

  @Test
  fun redirectPolicyRequiresExactGitHubAssetHostsAndHttps() {
    assertTrue(
      approvedDownloadRedirect(
        "https://release-assets.githubusercontent.com/github-production-release-asset/file?token=public-signed-url"
      )
    )
    assertFalse(approvedDownloadRedirect("http://release-assets.githubusercontent.com/file"))
    assertFalse(
      approvedDownloadRedirect("https://release-assets.githubusercontent.com.evil.test/file")
    )
    assertFalse(approvedDownloadRedirect("https://user@objects.githubusercontent.com/file"))
    assertFalse(approvedDownloadRedirect("https://objects.githubusercontent.com:444/file"))
  }

  @Test
  fun downloadedPackageSignerCodeAndAbiAreAllRechecked() {
    val offer = valid()
    fun verify(
      pkg: String = installed.applicationId,
      code: Long = 170,
      sig: Set<String> = installed.signers,
      abis: Set<String> = manifest.abis.toSet(),
      sdk: Int = 26,
    ) = validateDownloadedIdentity(offer, installed, pkg, code, sdk, sig, abis)
    verify()
    for (action in
      listOf<() -> Unit>(
        { verify(pkg = "other.app") },
        { verify(code = 169) },
        { verify(code = 171) },
        { verify(sig = setOf("d".repeat(64))) },
        { verify(sig = emptySet()) },
        { verify(abis = setOf("x86")) },
        { verify(sdk = 99) },
      )) {
      assertThrows(IllegalArgumentException::class.java, action)
    }
  }

  @Test
  fun automaticChecksAreThrottledAndClockChangesRecover() {
    val now = 10_000_000L
    assertTrue(shouldCheckUpdate(now, 0, 0))
    assertFalse(shouldCheckUpdate(now, now - 1000, 0))
    assertFalse(shouldCheckUpdate(now, now - 2_000_000, now - 1000))
    assertTrue(shouldCheckUpdate(now, now + 1000, now + 1000))
    assertTrue(shouldCheckUpdate(now + 30_000_000, now, now))
  }

  @Test
  fun releaseSelectionUsesMetadataCodeRatherThanReleaseOrder() = runTest {
    val older = release("1.4.8")
    val source = KaiReleaseSource { url, _ ->
      when {
        url == KAI_RELEASE_API -> Json.encodeToString(listOf(older, release()))
        url.contains("1.4.8") ->
          Json.encodeToString(manifest.copy(versionName = "1.4.8", versionCode = 169))
        else -> Json.encodeToString(manifest)
      }
    }
    val result = source.findUpdate(installed, true)!!
    assertEquals(170L, result.manifest.versionCode)
    assertEquals("Actual release notes", result.release.body)
    assertNull(source.findUpdate(installed, false))
  }

  @Test
  fun missingMetadataAndNoReleasesAreSafe() = runTest {
    assertNull(KaiReleaseSource { _, _ -> "[]" }.findUpdate(installed, true))
    assertNull(
      KaiReleaseSource { _, _ -> Json.encodeToString(listOf(release(metadata = false))) }
        .findUpdate(installed, true)
    )
  }

  @Test
  fun malformedMetadataFailsWithoutInventingAVersion() = runTest {
    val source = KaiReleaseSource { url, _ ->
      if (url == KAI_RELEASE_API) Json.encodeToString(listOf(release())) else "{}"
    }
    try {
      source.findUpdate(installed, true)
      fail("Malformed metadata must fail")
    } catch (expected: KaiUpdateFailure) {
      assertEquals(KaiUpdateFailure.Reason.METADATA, expected.reason)
    }
  }

  @Test
  fun malformedReleaseListReportsMetadataFailure() = runTest {
    try {
      KaiReleaseSource { _, _ -> "{}" }.findUpdate(installed, true)
      fail("Malformed release list must fail")
    } catch (expected: KaiUpdateFailure) {
      assertEquals(KaiUpdateFailure.Reason.METADATA, expected.reason)
    }
  }

  @Test
  fun networkFailuresAndCancellationAreNotConvertedToFakeUpdates() = runTest {
    for (reason in
      listOf(
        KaiUpdateFailure.Reason.NETWORK,
        KaiUpdateFailure.Reason.RATE_LIMIT,
        KaiUpdateFailure.Reason.SERVER,
      )) {
      try {
        KaiReleaseSource { _, _ -> throw KaiUpdateFailure(reason) }.findUpdate(installed, true)
        fail("Failure must propagate")
      } catch (expected: KaiUpdateFailure) {
        assertEquals(reason, expected.reason)
      }
    }
    try {
      KaiReleaseSource { _, _ -> throw CancellationException("cancelled") }
        .findUpdate(installed, true)
      fail("Cancellation must propagate")
    } catch (_: CancellationException) {}
  }
}
