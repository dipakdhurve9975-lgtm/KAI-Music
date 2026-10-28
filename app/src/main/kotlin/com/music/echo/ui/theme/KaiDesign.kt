/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.theme

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Shared rhythm for KAI surfaces, artwork and touch targets. Colors come from MaterialTheme. */
object KaiSpacing {
  val xxs = 4.dp
  val xs = 8.dp
  val sm = 12.dp
  val md = 16.dp
  val lg = 24.dp
  val xl = 32.dp
  val xxl = 48.dp
  val page = lg
  val minimumTouchTarget = 48.dp
  val rowMinHeight = 64.dp
}

object KaiShapes {
  val artwork = RoundedCornerShape(16.dp)
  val card = RoundedCornerShape(24.dp)
  val pill = RoundedCornerShape(50)
  val theme =
    Shapes(
      extraSmall = RoundedCornerShape(8.dp),
      small = RoundedCornerShape(12.dp),
      medium = artwork,
      large = card,
      extraLarge = RoundedCornerShape(32.dp),
    )
}

object KaiMotion {
  const val feedbackMillis = 140
  const val transitionMillis = 260
  const val ambientMillis = 450
  val easing = FastOutSlowInEasing
}

object KaiPalette {
  val Rose = Color(0xFFD85C78)
  val SoftRose = Color(0xFFF0A9B7)
  val DarkBackground = Color(0xFF100D10)
  val DarkSurface = Color(0xFF1C171C)
  val DarkElevated = Color(0xFF292127)
  val DarkText = Color(0xFFF8F3F5)
  val DarkSecondaryText = Color(0xFFAAA0A6)
  val LightBackground = Color(0xFFFFFAF7)
  val LightSurface = Color(0xFFF6EEF0)
  val LightElevated = Color(0xFFEFE5E9)
  val LightAccent = Color(0xFF8D314C)
  val LightText = Color(0xFF241C22)
  val LightSecondaryText = Color(0xFF6B5D65)
  /** Existing color-picker starting value; deliberately distinct from both palette sentinels. */
  val CustomAccentFallback = Color(0xFF1E88E5)
}

internal val LocalKaiReducedMotion = staticCompositionLocalOf { false }

/** One system observer is installed by the app theme; rows and buttons only read its value. */
@Composable
internal fun rememberKaiSystemReducedMotion(): Boolean {
  val context = LocalContext.current
  fun systemValue() =
    Settings.Global.getFloat(
      context.contentResolver,
      Settings.Global.ANIMATOR_DURATION_SCALE,
      1f,
    ) == 0f
  var reducedMotion by remember(context) { mutableStateOf(systemValue()) }
  DisposableEffect(context) {
    val observer =
      object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
          reducedMotion = systemValue()
        }
      }
    context.contentResolver.registerContentObserver(
      Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
      false,
      observer,
    )
    onDispose { context.contentResolver.unregisterContentObserver(observer) }
  }
  return reducedMotion
}

@Composable fun rememberKaiReducedMotion(): Boolean = LocalKaiReducedMotion.current

/** Pair with the same interactionSource used by clickable; no extra pointer or gesture handling. */
fun Modifier.kaiPressFeedback(
  interactionSource: MutableInteractionSource,
  enabled: Boolean = true,
): Modifier = composed {
  val pressed by interactionSource.collectIsPressedAsState()
  val reducedMotion = rememberKaiReducedMotion()
  val scale by
    animateFloatAsState(
      targetValue = if (enabled && pressed && !reducedMotion) 0.98f else 1f,
      animationSpec =
        tween(if (reducedMotion) 0 else KaiMotion.feedbackMillis, easing = KaiMotion.easing),
      label = "kaiPressFeedback",
    )
  graphicsLayer {
    scaleX = scale
    scaleY = scale
  }
}
