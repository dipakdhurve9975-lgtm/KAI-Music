package echo.music.iad1tya.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AutoLyricsResolvedStyleKey
import echo.music.iad1tya.constants.AutoLyricsStyleKey
import echo.music.iad1tya.constants.AutoLyricsTrackKey
import echo.music.iad1tya.constants.LyricsPresentationStyle
import echo.music.iad1tya.constants.LyricsPresentationStyleKey
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private data class SavedLyricsPresentation(
  val manual: LyricsPresentationStyle,
  val auto: Boolean,
  val track: String?,
  val resolved: LyricsPresentationStyle,
)

class LyricsPresentationController
internal constructor(
  val ready: Boolean,
  val style: LyricsPresentationStyle,
  val manualStyle: LyricsPresentationStyle,
  val auto: Boolean,
  val selectStyle: (LyricsPresentationStyle) -> Unit,
  val setAuto: (Boolean) -> Unit,
)

/** All values come from one DataStore snapshot, and preference changes are atomic. */
@Composable
fun rememberLyricsPresentation(trackId: String?): LyricsPresentationController {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val latestTrack by rememberUpdatedState(trackId)
  val saved by
    remember(context) {
        context.dataStore.data
          .map { prefs ->
            SavedLyricsPresentation(
              parseLyricsPresentation(prefs[LyricsPresentationStyleKey]),
              prefs[AutoLyricsStyleKey] ?: false,
              prefs[AutoLyricsTrackKey],
              parseLyricsPresentation(prefs[AutoLyricsResolvedStyleKey]),
            )
          }
          .distinctUntilChanged()
      }
      .collectAsState(initial = null)
  val manual = saved?.manual ?: LyricsPresentationStyle.CLASSIC_FLOW
  val style =
    if (saved?.auto == true && !trackId.isNullOrBlank() && saved?.track != trackId) {
      nextAutoLyricsPresentation(trackId, saved!!.resolved)
    } else
      resolveLyricsPresentation(
        manual,
        saved?.auto == true,
        trackId,
        saved?.track,
        saved?.resolved ?: LyricsPresentationStyle.CLASSIC_FLOW,
      )

  LaunchedEffect(trackId, saved?.auto, saved?.track) {
    val targetTrack = trackId
    if (saved?.auto == true && !targetTrack.isNullOrBlank() && saved?.track != targetTrack) {
      context.dataStore.edit { prefs ->
        if (
          latestTrack == targetTrack &&
            prefs[AutoLyricsStyleKey] == true &&
            prefs[AutoLyricsTrackKey] != targetTrack
        ) {
          prefs[AutoLyricsResolvedStyleKey] =
            nextAutoLyricsPresentation(
                targetTrack,
                parseLyricsPresentation(prefs[AutoLyricsResolvedStyleKey]),
              )
              .name
          prefs[AutoLyricsTrackKey] = targetTrack
        }
      }
    }
  }

  return LyricsPresentationController(
    ready = saved != null,
    style = style,
    manualStyle = manual,
    auto = saved?.auto == true,
    selectStyle = { selected ->
      scope.launch {
        context.dataStore.edit { prefs ->
          prefs[LyricsPresentationStyleKey] = selected.name
          prefs[AutoLyricsStyleKey] = false
          prefs[AutoLyricsResolvedStyleKey] = selected.name
          prefs[AutoLyricsTrackKey] = latestTrack.orEmpty()
        }
      }
    },
    setAuto = { enabled ->
      scope.launch {
        context.dataStore.edit { prefs ->
          val currentManual = parseLyricsPresentation(prefs[LyricsPresentationStyleKey])
          val currentStyle =
            if (
              prefs[AutoLyricsStyleKey] == true &&
                !latestTrack.isNullOrBlank() &&
                prefs[AutoLyricsTrackKey] != latestTrack
            ) {
              nextAutoLyricsPresentation(
                latestTrack!!,
                parseLyricsPresentation(prefs[AutoLyricsResolvedStyleKey]),
              )
            } else
              resolveLyricsPresentation(
                currentManual,
                prefs[AutoLyricsStyleKey] == true,
                latestTrack,
                prefs[AutoLyricsTrackKey],
                parseLyricsPresentation(prefs[AutoLyricsResolvedStyleKey]),
              )
          // Persist this pin so Lyrics → Normal → Lyrics and Ambient retain the same song style.
          prefs[AutoLyricsStyleKey] = enabled
          prefs[AutoLyricsTrackKey] = latestTrack.orEmpty()
          prefs[AutoLyricsResolvedStyleKey] = currentStyle.name
          if (!enabled) prefs[LyricsPresentationStyleKey] = currentStyle.name
        }
      }
    },
  )
}

@Composable
fun lyricsPresentationLabel(style: LyricsPresentationStyle): String =
  stringResource(
    when (style) {
      LyricsPresentationStyle.CLASSIC_FLOW -> R.string.kai_lyrics_classic
      LyricsPresentationStyle.FLOATING -> R.string.kai_lyrics_floating
      LyricsPresentationStyle.CINEMATIC -> R.string.kai_lyrics_cinematic
      LyricsPresentationStyle.KINETIC -> R.string.kai_lyrics_kinetic
      LyricsPresentationStyle.MINIMAL_FOCUS -> R.string.kai_lyrics_minimal
    }
  )

/** Reused from existing Lyrics menu, Appearance and Ambient settings. No extra destination. */
@Composable
fun LyricsPresentationDialog(
  controller: LyricsPresentationController,
  onDismiss: () -> Unit,
) {
  DefaultDialog(
    onDismiss = onDismiss,
    title = { Text(stringResource(R.string.kai_lyrics_presentation)) },
    buttons = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) } },
  ) {
    Column(Modifier.verticalScroll(rememberScrollState()).selectableGroup()) {
      LyricsPresentationStyle.entries.forEach { style ->
        Row(
          Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
              selected = controller.style == style && !controller.auto,
              role = Role.RadioButton,
              onClick = { controller.selectStyle(style) },
            )
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          RadioButton(selected = controller.style == style && !controller.auto, onClick = null)
          Text(lyricsPresentationLabel(style), Modifier.padding(start = 12.dp))
        }
      }
      Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(top = 8.dp).clickable(
          role = Role.Switch
        ) {
          controller.setAuto(!controller.auto)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          stringResource(R.string.kai_lyrics_auto),
          Modifier.weight(1f),
          style = MaterialTheme.typography.titleSmall,
        )
        Switch(checked = controller.auto, onCheckedChange = controller.setAuto)
      }
      Text(
        stringResource(R.string.kai_lyrics_auto_description),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
      )
      if (controller.style == LyricsPresentationStyle.KINETIC) {
        Text(
          stringResource(R.string.kai_lyrics_kinetic_description),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp),
        )
      }
    }
  }
}
