/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/** Presentation is independent of the existing, saved Classic animation preference. */
enum class LyricsPresentationStyle {
  CLASSIC_FLOW,
  FLOATING,
  CINEMATIC,
  KINETIC,
  MINIMAL_FOCUS,
}

val LyricsPresentationStyleKey = stringPreferencesKey("kai_lyrics_presentation_style")
val AutoLyricsStyleKey = booleanPreferencesKey("kai_auto_lyrics_style")
val AutoLyricsTrackKey = stringPreferencesKey("kai_auto_lyrics_track")
val AutoLyricsResolvedStyleKey = stringPreferencesKey("kai_auto_lyrics_resolved_style")
