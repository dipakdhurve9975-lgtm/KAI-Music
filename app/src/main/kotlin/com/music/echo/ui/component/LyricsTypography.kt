/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.LyricsGlowEffectKey
import echo.music.iad1tya.ui.theme.OutfitFontFamily
import echo.music.iad1tya.ui.theme.PlusJakartaSansFontFamily
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference

private val LyricsTypefaceKey = stringPreferencesKey("kaiLyricsTypeface")

internal enum class LyricsTypeface {
  FOLLOW_THEME,
  SYSTEM,
  JAKARTA,
  OUTFIT,
  SERIF,
}

@Composable
private fun LyricsTypeface.family(): FontFamily =
  when (this) {
    LyricsTypeface.FOLLOW_THEME ->
      MaterialTheme.typography.bodyLarge.fontFamily ?: FontFamily.Default
    LyricsTypeface.SYSTEM -> FontFamily.Default
    LyricsTypeface.JAKARTA -> PlusJakartaSansFontFamily
    LyricsTypeface.OUTFIT -> OutfitFontFamily
    LyricsTypeface.SERIF -> FontFamily.Serif
  }

@Composable
private fun LyricsTypeface.label(): String =
  stringResource(
    when (this) {
      LyricsTypeface.FOLLOW_THEME -> R.string.follow_theme
      LyricsTypeface.SYSTEM -> R.string.kai_lyrics_font_system
      LyricsTypeface.JAKARTA -> R.string.kai_lyrics_font_jakarta
      LyricsTypeface.OUTFIT -> R.string.kai_lyrics_font_outfit
      LyricsTypeface.SERIF -> R.string.kai_lyrics_font_serif
    }
  )

@Composable
internal fun rememberLyricsTypeface(): FontFamily {
  val selected by rememberEnumPreference(LyricsTypefaceKey, LyricsTypeface.FOLLOW_THEME)
  return selected.family()
}

/** A bounded, static text shadow. No duplicate blurred text or perpetual glow animation. */
internal fun lyricGlowShadow(color: Color, enabled: Boolean): Shadow? =
  if (enabled) Shadow(color = color.copy(alpha = .38f), blurRadius = 10f) else null

/** Reuses the existing presentation dialog rather than adding another settings destination. */
@Composable
internal fun LyricsTypographyControls() {
  var selected by rememberEnumPreference(LyricsTypefaceKey, LyricsTypeface.FOLLOW_THEME)
  var glow by rememberPreference(LyricsGlowEffectKey, false)
  var expanded by remember { mutableStateOf(false) }
  Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Text(
        stringResource(R.string.kai_lyrics_font),
        Modifier.weight(1f),
        style = MaterialTheme.typography.titleSmall,
      )
      Box {
        TextButton(onClick = { expanded = true }) { Text(selected.label()) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          LyricsTypeface.entries.forEach { typeface ->
            DropdownMenuItem(
              text = { Text(typeface.label(), fontFamily = typeface.family()) },
              onClick = {
                selected = typeface
                expanded = false
              },
            )
          }
        }
      }
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Text(
        stringResource(R.string.kai_lyrics_glow),
        Modifier.weight(1f),
        style = MaterialTheme.typography.titleSmall,
      )
      Switch(checked = glow, onCheckedChange = { glow = it })
    }
    Text(
      stringResource(R.string.kai_lyrics_font_preview),
      modifier = Modifier.padding(vertical = 12.dp),
      style =
        MaterialTheme.typography.titleLarge.copy(
          fontFamily = selected.family(),
          shadow = lyricGlowShadow(MaterialTheme.colorScheme.onSurface, glow),
        ),
    )
  }
}
