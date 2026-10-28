/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import echo.music.iad1tya.ui.theme.KaiPalette
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.isActive

/** Muted album color, with cached light fields moved as a layer rather than rebuilt every frame. */
@Composable
fun KaiAlbumAtmosphere(colors: List<Color>, active: Boolean, modifier: Modifier = Modifier) {
  val palette =
    remember(colors) {
      colors
        .ifEmpty { listOf(KaiPalette.DarkElevated) }
        .map { source ->
          val gray = (source.red * 0.2126f + source.green * 0.7152f + source.blue * 0.0722f)
          lerp(lerp(source, Color(gray, gray, gray), 0.25f), KaiPalette.DarkBackground, 0.58f)
        }
    }
  val gradient =
    remember(palette) {
      Brush.verticalGradient(
        listOf(
          palette.first(),
          palette[palette.size / 2],
          palette.last(),
          KaiPalette.DarkBackground,
        )
      )
    }
  val phase = rememberKaiAtmosphereProgress(active, 60000)
  Box(modifier.background(gradient)) {
    Box(
      Modifier.fillMaxSize()
        .graphicsLayer {
          val radians = phase.value * (2f * PI.toFloat())
          translationX = sin(radians) * size.width * 0.035f
          translationY = cos(radians) * size.height * 0.025f
        }
        .drawWithCache {
          val upper =
            Brush.radialGradient(
              listOf(palette.first().copy(alpha = 0.45f), Color.Transparent),
              center = Offset(size.width * 0.18f, size.height * 0.24f),
              radius = size.maxDimension * 0.65f,
            )
          val lower =
            Brush.radialGradient(
              listOf(palette.last().copy(alpha = 0.32f), Color.Transparent),
              center = Offset(size.width * 0.88f, size.height * 0.6f),
              radius = size.maxDimension * 0.55f,
            )
          onDrawBehind {
            drawRect(upper)
            drawRect(lower)
          }
        }
    )
  }
}

/** Shared decorative clock: retained phase, no frame work while paused or offscreen. */
@Composable
internal fun rememberKaiAtmosphereProgress(active: Boolean, durationMillis: Int): State<Float> {
  val phase = remember { Animatable(0f) }
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val reducedMotion = rememberKaiReducedMotion()
  LaunchedEffect(active, reducedMotion, lifecycle, durationMillis) {
    if (active && !reducedMotion) {
      lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        while (isActive) {
          phase.animateTo(1f, tween(durationMillis, easing = LinearEasing))
          phase.snapTo(0f)
        }
      }
    }
  }
  return phase.asState()
}
