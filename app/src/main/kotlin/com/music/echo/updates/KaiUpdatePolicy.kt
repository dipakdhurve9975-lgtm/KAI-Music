// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.updates

import java.net.URI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val KAI_REPO = "dipakdhurve9975-lgtm/KAI-Music"
internal const val KAI_RELEASES = "https://github.com/$KAI_REPO/releases"
internal const val KAI_RELEASE_API = "https://api.github.com/repos/$KAI_REPO/releases?per_page=30"
internal const val MAX_APK_BYTES = 512L * 1024 * 1024

@Serializable
internal data class KaiReleaseAsset(
  val name: String,
  val size: Long,
  @SerialName("browser_download_url") val url: String,
  val state: String = "uploaded",
)

@Serializable
internal data class KaiRelease(
  @SerialName("tag_name") val tag: String,
  val draft: Boolean = false,
  val prerelease: Boolean = false,
  @SerialName("html_url") val url: String,
  val body: String? = null,
  val assets: List<KaiReleaseAsset> = emptyList(),
)

@Serializable
internal data class KaiUpdateManifest(
  @SerialName("schema_version") val schema: Int,
  @SerialName("application_id") val applicationId: String,
  @SerialName("version_code") val versionCode: Long,
  @SerialName("version_name") val versionName: String,
  val channel: String,
  val variant: String,
  @SerialName("min_sdk") val minSdk: Int,
  val abis: List<String>,
  @SerialName("apk_file_name") val apkFileName: String,
  @SerialName("apk_sha256") val sha256: String,
  @SerialName("signer_sha256") val signerSha256: String,
  @SerialName("source_commit") val sourceCommit: String,
)

internal data class KaiInstalledIdentity(
  val applicationId: String,
  val versionCode: Long,
  val sdk: Int,
  val abis: List<String>,
  val signers: Set<String>,
)

internal data class KaiUpdateOffer(
  val release: KaiRelease,
  val manifest: KaiUpdateManifest,
  val apk: KaiReleaseAsset,
)

internal fun approvedRelease(release: KaiRelease, includePreview: Boolean): Boolean {
  val prefix = if (release.prerelease) "kai-preview-" else "kai-v"
  return !release.draft &&
    (!release.prerelease || includePreview) &&
    release.tag.matches(Regex("${prefix}\\d{1,3}\\.\\d+\\.\\d+")) &&
    release.url == "$KAI_RELEASES/tag/${release.tag}"
}

internal fun trustedAsset(release: KaiRelease, asset: KaiReleaseAsset): Boolean =
  asset.state == "uploaded" &&
    asset.size in 1..MAX_APK_BYTES &&
    asset.name.matches(Regex("[A-Za-z0-9._-]+")) &&
    asset.url == "$KAI_RELEASES/download/${release.tag}/${asset.name}"

/** Only GitHub's release service may redirect a previously validated repository asset. */
internal fun approvedDownloadRedirect(url: String): Boolean = runCatching {
  val uri = URI(url)
  uri.scheme == "https" &&
    uri.rawUserInfo == null &&
    uri.port in listOf(-1, 443) &&
    uri.fragment == null &&
    uri.host in
      setOf(
        "release-assets.githubusercontent.com",
        "objects.githubusercontent.com",
      )
}
  .getOrDefault(false)

internal fun validateOffer(
  release: KaiRelease,
  manifest: KaiUpdateManifest,
  installed: KaiInstalledIdentity,
  includePreview: Boolean,
): KaiUpdateOffer? {
  if (!approvedRelease(release, includePreview)) return null
  val expectedChannel = if (release.prerelease) "preview" else "stable"
  val expectedTag = (if (release.prerelease) "kai-preview-" else "kai-v") + manifest.versionName
  val alias = if (release.prerelease) "KAI-universal-gms-preview.apk" else "KAI-latest.apk"
  if (
    manifest.schema != 1 ||
      release.tag != expectedTag ||
      manifest.channel != expectedChannel ||
      manifest.applicationId != installed.applicationId ||
      manifest.versionCode <= installed.versionCode ||
      manifest.versionName.isBlank() ||
      manifest.minSdk !in 1..installed.sdk ||
      manifest.variant != "universalGms" ||
      manifest.apkFileName != alias ||
      manifest.abis.none { it in installed.abis } ||
      !manifest.sha256.matches(Regex("[a-f0-9]{64}")) ||
      manifest.signerSha256 !in installed.signers ||
      !manifest.sourceCommit.matches(Regex("[a-f0-9]{40}"))
  )
    return null
  val asset = release.assets.singleOrNull { it.name == manifest.apkFileName } ?: return null
  return if (trustedAsset(release, asset)) KaiUpdateOffer(release, manifest, asset) else null
}

internal fun validateDownloadedIdentity(
  offer: KaiUpdateOffer,
  installed: KaiInstalledIdentity,
  packageName: String,
  versionCode: Long,
  minSdk: Int,
  signers: Set<String>,
  actualAbis: Set<String>,
) {
  require(packageName == installed.applicationId && packageName == offer.manifest.applicationId) {
    "Wrong application"
  }
  require(versionCode > installed.versionCode && versionCode == offer.manifest.versionCode) {
    "Wrong version"
  }
  require(minSdk == offer.manifest.minSdk && minSdk <= installed.sdk) {
    "Android version incompatible"
  }
  // This distribution retains its existing signer; key rotation needs a separate reviewed
  // migration.
  require(
    signers.isNotEmpty() &&
      signers == installed.signers &&
      signers == setOf(offer.manifest.signerSha256)
  ) {
    "Signing certificate mismatch"
  }
  require(actualAbis == offer.manifest.abis.toSet() && actualAbis.any { it in installed.abis }) {
    "APK variant incompatible"
  }
}

/** Published debug APKs are previews; keep explicit saved choices, including opt-outs. */
internal fun includePreviewUpdates(previewBuild: Boolean, saved: Boolean?): Boolean =
  saved ?: previewBuild

internal fun shouldCheckUpdateForChannel(
  manual: Boolean,
  preview: Boolean,
  lastPreview: Boolean?,
  now: Long,
  lastAttempt: Long,
  lastSuccess: Long,
): Boolean = manual || lastPreview != preview || shouldCheckUpdate(now, lastAttempt, lastSuccess)

internal fun shouldCheckUpdate(now: Long, lastAttempt: Long, lastSuccess: Long): Boolean =
  (lastAttempt == 0L || now < lastAttempt || now - lastAttempt >= 30 * 60 * 1000L) &&
    (lastSuccess == 0L || now < lastSuccess || now - lastSuccess >= 60 * 60 * 1000L)
