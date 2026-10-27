package echo.music.iad1tya.ui.screens.ambient

import androidx.datastore.preferences.core.stringPreferencesKey

/** Ambient presentation is independent of the full player's session-only display mode. */
enum class AmbientPresentation {
  NORMAL,
  VINYL,
}

val AmbientPresentationKey = stringPreferencesKey("kai_ambient_presentation")
