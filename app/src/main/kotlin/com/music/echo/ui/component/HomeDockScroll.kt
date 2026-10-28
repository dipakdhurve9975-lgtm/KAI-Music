/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** Direction hysteresis uses consumed content motion, never intercepting a scrolling gesture. */
internal class HomeDockScrollPolicy(private val threshold: Float) {
  private var accumulated = 0f
  var hidden = false
    private set

  fun reveal() {
    accumulated = 0f
    hidden = false
  }

  fun onScroll(consumedY: Float): Boolean {
    if (!consumedY.isFinite() || consumedY == 0f) return hidden
    if (accumulated * consumedY < 0f) accumulated = 0f
    accumulated += consumedY
    if (accumulated <= -threshold) {
      hidden = true
      accumulated = -threshold
    }
    if (accumulated >= threshold) {
      hidden = false
      accumulated = threshold
    }
    return hidden
  }
}

@Composable
internal fun rememberHomeDockScroll(
  enabled: Boolean,
  resetKey: Any?,
  reducedMotion: Boolean,
): Pair<State<Float>, NestedScrollConnection> {
  val threshold = with(LocalDensity.current) { 12.dp.toPx() }
  val policy = remember(threshold) { HomeDockScrollPolicy(threshold) }
  var hidden by remember { mutableStateOf(false) }
  val active by rememberUpdatedState(enabled)
  LaunchedEffect(enabled, resetKey) {
    policy.reveal()
    hidden = false
  }
  val connection =
    remember(policy) {
      object : NestedScrollConnection {
        override fun onPostScroll(
          consumed: Offset,
          available: Offset,
          source: NestedScrollSource,
        ): Offset {
          if (active) hidden = policy.onScroll(consumed.y)
          return Offset.Zero
        }
      }
    }
  val fraction =
    animateFloatAsState(
      if (enabled && hidden) 1f else 0f,
      tween(if (reducedMotion) 0 else if (hidden) 180 else 220),
      label = "homeDockVisibility",
    )
  return fraction to connection
}
