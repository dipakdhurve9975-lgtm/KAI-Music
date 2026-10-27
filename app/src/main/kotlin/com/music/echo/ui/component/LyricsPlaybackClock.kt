package echo.music.iad1tya.ui.component

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.Player
import echo.music.iad1tya.playback.PlayerConnection
import kotlinx.coroutines.isActive

internal class LyricsPlaybackClock(val position: State<Long>, val seeking: State<Boolean>)

/** One visible lyric timeline. Paused seeks are event-driven; no frame loop runs while paused. */
@Composable
internal fun rememberLyricsPlaybackClock(
  connection: PlayerConnection,
  trackId: String?,
  visible: Boolean,
  sliderPositionProvider: () -> Long?,
): LyricsPlaybackClock {
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val player = connection.player
  val cast = remember(connection) { connection.service.castConnectionHandler }
  val casting by cast?.isCasting?.collectAsState() ?: remember { mutableStateOf(false) }
  val castPlaying by cast?.castIsPlaying?.collectAsState() ?: remember { mutableStateOf(false) }
  val castBuffering by cast?.castIsBuffering?.collectAsState() ?: remember { mutableStateOf(false) }
  val castPosition by cast?.castPosition?.collectAsState() ?: remember { mutableLongStateOf(0L) }
  var localPlaying by remember(player) { mutableStateOf(player.isPlaying) }
  val position =
    remember(connection) { mutableLongStateOf(player.currentPosition.coerceAtLeast(0L)) }
  val seeking = remember { mutableStateOf(false) }
  val latestSlider by rememberUpdatedState(sliderPositionProvider)
  val latestCasting by rememberUpdatedState(casting)
  val latestCastPosition by rememberUpdatedState(castPosition)
  val advancing = if (casting) castPlaying && !castBuffering else localPlaying
  val latestAdvancing by rememberUpdatedState(advancing)
  var castSampleTime by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }

  fun sample() {
    if (!visible || !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
    val preview = latestSlider()
    seeking.value = preview != null
    position.longValue =
      (preview
          ?: if (latestCasting) {
            // Interpolate only between real receiver samples while the receiver reports playing.
            latestCastPosition +
              if (latestAdvancing) {
                (SystemClock.elapsedRealtime() - castSampleTime).coerceIn(0L, 600L)
              } else 0L
          } else player.currentPosition)
        .coerceAtLeast(0L)
  }
  val latestSample by rememberUpdatedState(::sample)
  DisposableEffect(player, lifecycle, visible) {
    val listener =
      object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
          localPlaying = player.isPlaying
          latestSample()
        }
      }
    if (visible) player.addListener(listener)
    onDispose { if (visible) player.removeListener(listener) }
  }
  LaunchedEffect(castPosition, casting, castPlaying, castBuffering, trackId) {
    castSampleTime = SystemClock.elapsedRealtime()
    sample()
  }
  LaunchedEffect(visible, lifecycle, connection) {
    if (visible)
      lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        localPlaying = player.isPlaying
        snapshotFlow { latestSlider() }.collect { latestSample() }
      }
  }
  LaunchedEffect(visible, advancing, lifecycle, connection) {
    if (visible && advancing)
      lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        while (isActive) {
          withFrameMillis { latestSample() }
        }
      }
  }
  return remember(position, seeking) { LyricsPlaybackClock(position, seeking) }
}
