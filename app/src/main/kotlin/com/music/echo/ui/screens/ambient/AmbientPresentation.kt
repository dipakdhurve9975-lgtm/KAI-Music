/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.ambient

import androidx.datastore.preferences.core.stringPreferencesKey

/** Ambient presentation is independent of the full player's session-only display mode. */
enum class AmbientPresentation {
  NORMAL,
  VINYL,
}

val AmbientPresentationKey = stringPreferencesKey("kai_ambient_presentation")
