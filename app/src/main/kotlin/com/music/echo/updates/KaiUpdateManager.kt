// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.updates

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import echo.music.iad1tya.BuildConfig
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

internal enum class UpdateCheck {
  IDLE,
  CHECKING,
  CURRENT,
  AVAILABLE,
  ERROR,
}

internal enum class UpdateTransfer {
  IDLE,
  DOWNLOADING,
  VERIFYING,
  READY,
  ERROR,
}

internal data class KaiUpdateState(
  val check: UpdateCheck = UpdateCheck.IDLE,
  val offer: KaiUpdateOffer? = null,
  val showDialog: Boolean = false,
  val includePreview: Boolean = false,
  val transfer: UpdateTransfer = UpdateTransfer.IDLE,
  val downloaded: Long = 0L,
  val failure: KaiUpdateFailure.Reason? = null,
  val lastSuccess: Long = 0L,
)

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface KaiUpdateEntryPoint {
  fun updates(): KaiUpdateManager
}

/** One process-wide check/download owner; never creates a second music session or clears data. */
@Singleton
internal class KaiUpdateManager
@Inject
constructor(@ApplicationContext private val context: Context) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val preferences = context.getSharedPreferences("kai_updates_v1", Context.MODE_PRIVATE)
  private val repository = KaiUpdateRepository()
  private val verifier = KaiApkVerifier(context)
  private val checkMutex = Mutex()
  private var downloadJob: Job? = null
  private var installJob: Job? = null
  private var readyFile: File? = null
  private val mutableState =
    MutableStateFlow(
      KaiUpdateState(includePreview = includePreviewUpdates(BuildConfig.DEBUG, null))
    )
  val state = mutableState.asStateFlow()

  init {
    scope.launch {
      mutableState.update {
        it.copy(
          includePreview = previewPreference(),
          lastSuccess = preferences.getLong("last_success", 0L),
        )
      }
    }
  }

  fun check(manual: Boolean = false) {
    scope.launch {
      if (!checkMutex.tryLock()) return@launch
      try {
        if (state.value.transfer in setOf(UpdateTransfer.DOWNLOADING, UpdateTransfer.VERIFYING))
          return@launch
        val now = System.currentTimeMillis()
        val preview = previewPreference()
        val previousChannel =
          if (preferences.contains("last_checked_preview"))
            preferences.getBoolean("last_checked_preview", false)
          else null
        if (
          !shouldCheckUpdateForChannel(
            manual,
            preview,
            previousChannel,
            now,
            preferences.getLong("last_attempt", 0L),
            preferences.getLong("last_success", 0L),
          )
        )
          return@launch
        preferences
          .edit()
          .putLong("last_attempt", now)
          .putBoolean("last_checked_preview", preview)
          .apply { if (previousChannel != preview) remove("last_success") }
          .apply()
        mutableState.update {
          it.copy(
            check = UpdateCheck.CHECKING,
            failure = null,
            includePreview = preview,
            lastSuccess = if (previousChannel == preview) it.lastSuccess else 0L,
          )
        }
        val offer = repository.source.findUpdate(verifier.installed(), preview)
        val dismissed =
          preferences.getLong("dismissed_" + (offer?.manifest?.channel ?: "stable"), -1L)
        preferences.edit().putLong("last_success", now).apply()
        mutableState.update {
          it.copy(
            check = if (offer == null) UpdateCheck.CURRENT else UpdateCheck.AVAILABLE,
            offer = offer,
            lastSuccess = now,
            failure = null,
            showDialog = offer != null && (manual || offer.manifest.versionCode != dismissed),
            transfer = UpdateTransfer.IDLE,
            downloaded = 0L,
          )
        }
      } catch (cancelled: CancellationException) {
        throw cancelled
      } catch (error: Exception) {
        mutableState.update {
          it.copy(
            check = UpdateCheck.ERROR,
            failure = (error as? KaiUpdateFailure)?.reason ?: KaiUpdateFailure.Reason.NETWORK,
          )
        }
      } finally {
        checkMutex.unlock()
      }
    }
  }

  private fun previewPreference(): Boolean =
    includePreviewUpdates(
      BuildConfig.DEBUG,
      if (preferences.contains("include_preview")) preferences.getBoolean("include_preview", false)
      else null,
    )

  fun setPreview(enabled: Boolean) {
    scope.launch {
      if (!checkMutex.tryLock()) return@launch
      try {
        if (state.value.transfer in setOf(UpdateTransfer.DOWNLOADING, UpdateTransfer.VERIFYING))
          return@launch
        preferences
          .edit()
          .putBoolean("include_preview", enabled)
          .remove("last_attempt")
          .remove("last_success")
          .apply()
        mutableState.update { KaiUpdateState(includePreview = enabled) }
      } finally {
        checkMutex.unlock()
      }
      check(manual = true)
    }
  }

  fun showOffer() {
    mutableState.update { it.copy(showDialog = it.offer != null) }
  }

  fun dismiss() {
    val offer = state.value.offer
    mutableState.update { it.copy(showDialog = false) }
    if (offer != null)
      scope.launch {
        preferences
          .edit()
          .putLong("dismissed_" + offer.manifest.channel, offer.manifest.versionCode)
          .apply()
      }
  }

  fun download() {
    if (downloadJob?.isCompleted == false) return
    downloadJob = scope.launch {
      if (!checkMutex.tryLock()) return@launch
      val offer =
        try {
          val selected = state.value.offer ?: return@launch
          readyFile = null
          mutableState.update {
            it.copy(
              showDialog = true,
              transfer = UpdateTransfer.DOWNLOADING,
              downloaded = 0L,
              failure = null,
            )
          }
          selected
        } finally {
          checkMutex.unlock()
        }
      val directory = File(context.cacheDir, "kai-updates").apply { mkdirs() }
      val partial = File(directory, "update-${offer.manifest.versionCode}.partial")
      val target = File(directory, "update-${offer.manifest.versionCode}.apk")
      try {
        // Delete only our own updater cache, never music/downloads/preferences.
        directory.listFiles()?.filter { it != partial }?.forEach { it.delete() }
        repository.download(offer, partial) { received ->
          mutableState.update { it.copy(downloaded = received) }
        }
        mutableState.update { it.copy(transfer = UpdateTransfer.VERIFYING) }
        verifier.verify(partial, offer)
        kotlin.check(partial.renameTo(target)) { "Could not finish APK" }
        readyFile = target
        mutableState.update { it.copy(transfer = UpdateTransfer.READY, failure = null) }
      } catch (cancelled: CancellationException) {
        mutableState.update { it.copy(transfer = UpdateTransfer.IDLE, downloaded = 0L) }
        throw cancelled
      } catch (error: Exception) {
        target.delete()
        mutableState.update {
          it.copy(
            transfer = UpdateTransfer.ERROR,
            failure =
              (error as? KaiUpdateFailure)?.reason
                ?: if (error is IllegalArgumentException) KaiUpdateFailure.Reason.VERIFY
                else KaiUpdateFailure.Reason.DOWNLOAD,
          )
        }
      } finally {
        partial.delete()
      }
    }
  }

  fun cancelDownload() {
    downloadJob?.cancel()
  }

  fun install() {
    if (state.value.transfer != UpdateTransfer.READY || installJob?.isCompleted == false) return
    val file = readyFile ?: return
    val offer = state.value.offer ?: return
    installJob = scope.launch {
      try {
        verifier.verify(file, offer) // Recheck after any permission/settings round trip.
        withContext(Dispatchers.Main) {
          val intent =
            if (!context.packageManager.canRequestPackageInstalls()) {
              Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}"),
              )
            } else {
              val uri = FileProvider.getUriForFile(context, "${context.packageName}.updates", file)
              Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
          context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
      } catch (cancelled: CancellationException) {
        throw cancelled
      } catch (error: Exception) {
        mutableState.update {
          it.copy(transfer = UpdateTransfer.ERROR, failure = KaiUpdateFailure.Reason.INSTALL)
        }
      }
    }
  }
}
