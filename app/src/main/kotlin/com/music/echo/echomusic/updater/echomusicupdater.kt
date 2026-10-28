/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.echomusic.updater

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.constants.EnableCronetKey
import echo.music.iad1tya.constants.ForceOpusKey
import echo.music.iad1tya.echomusic.UpdateNotificationHelper
import echo.music.iad1tya.ui.screens.settings.UpdateSettings
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

data class ChangelogSection(val title: String, val items: List<String>)

/** Retains the old route for saved navigation entries without activating the retired updater. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navController: NavHostController) {
  UpdateSettings(navController, TopAppBarDefaults.exitUntilCollapsedScrollBehavior())
}

/**
 * Retire inherited update state/work separately from the KAI-only updater. Music, downloads and
 * playback notifications survive. Initialize only absent transport defaults once; later upgrades
 * preserve explicit choices. Run outside the main thread, including the first preferences read.
 */
suspend fun initializeKaiUpdatePlaceholder(context: Context) =
  withContext(Dispatchers.IO) {
    UpdateNotificationHelper.clearLegacyUpdateNotifications(context)
    val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    if (!preferences.getBoolean("kai_transport_defaults_migrated", false)) {
      // A version update must not overwrite explicit user choices from an older KAI build.
      context.dataStore.edit { addMissingKaiTransportDefaults(it) }
      preferences.edit().putBoolean("kai_transport_defaults_migrated", true).apply()
    }
    preferences
      .edit()
      .putBoolean("auto_update_check", false)
      .putBoolean("update_notifications", false)
      .putBoolean("update_available", false)
      .putString("last_seen_changelog_version", BuildConfig.VERSION_NAME)
      .apply()

    try {
      val workManager = WorkManager.getInstance(context)
      workManager.cancelUniqueWork("update_download").result.get()
      workManager.cancelAllWorkByTag("update_download").result.get()
    } catch (cancelled: CancellationException) {
      throw cancelled
    } catch (error: Exception) {
      // The retained no-op worker also stops any old persisted job if cancellation is unavailable.
      Timber.w(error, "Could not cancel retired app-update work")
    }
  }

internal fun addMissingKaiTransportDefaults(preferences: MutablePreferences) {
  if (preferences[ForceOpusKey] == null) preferences[ForceOpusKey] = true
  if (preferences[EnableCronetKey] == null) preferences[EnableCronetKey] = true
}
