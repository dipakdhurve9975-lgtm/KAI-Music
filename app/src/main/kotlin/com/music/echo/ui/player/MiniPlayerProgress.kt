package echo.music.iad1tya.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import echo.music.iad1tya.playback.PlayerConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal fun miniPlayerProgress(positionMs: Long, durationMs: Long): Float =
  if (durationMs <= 0L) 0f else (positionMs.toDouble() / durationMs).coerceIn(0.0, 1.0).toFloat()

/** Read this State in drawing, so progress doesn't recompose metadata/artwork every tick. */
@Composable
fun rememberMiniPlayerProgress(
  connection: PlayerConnection,
  trackId: String?,
  durationSeconds: Int,
): State<Float> {
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  return produceState(0f, connection, trackId, lifecycle, durationSeconds) {
    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
      while (isActive) {
        val cast = runCatching { connection.service.castConnectionHandler }.getOrNull()
        val casting = cast?.isCasting?.value == true
        val position =
          if (casting) cast?.castPosition?.value ?: 0L else connection.player.currentPosition
        val duration = if (casting) cast?.castDuration?.value ?: 0L else connection.player.duration
        value =
          miniPlayerProgress(
            position,
            duration.takeIf { it > 0L } ?: durationSeconds.toLong() * 1000L,
          )
        delay(250L)
      }
    }
  }
}
