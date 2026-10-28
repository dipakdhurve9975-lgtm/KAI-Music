/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.echomusic

import android.app.NotificationManager
import android.content.Context

object UpdateNotificationHelper {
  // Keep old PendingIntents valid across an in-place upgrade.
  const val ACTION_OPEN_ABOUT = "echo.music.iad1tya.action.OPEN_KAI_ABOUT"

  fun clearLegacyUpdateNotifications(context: Context) {
    context.getSystemService(NotificationManager::class.java)?.let { manager ->
      manager.cancel(1001) // Inherited update-available alert.
      manager.cancel(5678) // Inherited APK update download, never music downloads/playback.
    }
  }
}
