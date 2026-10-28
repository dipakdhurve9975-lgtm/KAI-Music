// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.updates

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

@Suppress("DEPRECATION")
internal class KaiApkVerifier(private val context: Context) {
  private val flags =
    if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
    else PackageManager.GET_SIGNATURES

  fun installed(): KaiInstalledIdentity {
    val info = context.packageManager.getPackageInfo(context.packageName, flags)
    return KaiInstalledIdentity(
      context.packageName,
      info.code(),
      Build.VERSION.SDK_INT,
      Build.SUPPORTED_ABIS.toList(),
      info.signerDigests(),
    )
  }

  fun verify(file: File, offer: KaiUpdateOffer) {
    require(file.isFile && file.length() == offer.apk.size && file.length() > 0L)
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
      val buffer = ByteArray(64 * 1024)
      while (true) {
        val count = input.read(buffer)
        if (count < 0) break
        digest.update(buffer, 0, count)
      }
    }
    require(digest.digest().toHex() == offer.manifest.sha256) { "Checksum mismatch" }
    val archive =
      context.packageManager.getPackageArchiveInfo(file.absolutePath, flags)
        ?: throw IllegalArgumentException("Unreadable APK")
    val abis =
      ZipFile(file).use { zip ->
        zip
          .entries()
          .asSequence()
          .map { it.name }
          .filter { it.startsWith("lib/") && it.endsWith(".so") }
          .map { it.substringAfter("lib/").substringBefore('/') }
          .toSet()
      }
    validateDownloadedIdentity(
      offer,
      installed(),
      archive.packageName,
      archive.code(),
      archive.applicationInfo?.minSdkVersion ?: Int.MAX_VALUE,
      archive.signerDigests(),
      abis,
    )
  }

  private fun PackageInfo.code(): Long =
    if (Build.VERSION.SDK_INT >= 28) longVersionCode else versionCode.toLong()

  private fun PackageInfo.signerDigests(): Set<String> {
    val certificates =
      if (Build.VERSION.SDK_INT >= 28) signingInfo?.apkContentsSigners else signatures
    return certificates
      .orEmpty()
      .map {
        MessageDigest.getInstance("SHA-256").digest(it.toByteArray()).toHex()
      }
      .toSet()
  }
}
