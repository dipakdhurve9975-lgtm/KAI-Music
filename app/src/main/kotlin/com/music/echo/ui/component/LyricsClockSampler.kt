// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * The clock is created before async lyrics become timed/visible. Its retained callbacks must read
 * that later visibility, not capture the initial false value in a local function reference.
 */
@Composable
internal fun rememberLyricsClockSampler(
  enabled: Boolean,
  resumed: () -> Boolean,
  preview: () -> Long?,
  position: () -> Long,
  accept: (Long, Boolean) -> Unit,
): () -> Unit {
  val currentEnabled = rememberUpdatedState(enabled)
  val currentResumed = rememberUpdatedState(resumed)
  val currentPreview = rememberUpdatedState(preview)
  val currentPosition = rememberUpdatedState(position)
  val currentAccept = rememberUpdatedState(accept)
  return remember {
    {
      if (currentEnabled.value && currentResumed.value()) {
        val seek = currentPreview.value()
        currentAccept.value((seek ?: currentPosition.value()).coerceAtLeast(0L), seek != null)
      }
    }
  }
}
