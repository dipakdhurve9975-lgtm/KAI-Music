/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.isActive

/**
 * Only compose during real work. The spiral path is cached; rotation updates its graphics layer.
 */
@Composable
fun KaiSpiralLoading(
  modifier: Modifier = Modifier.size(32.dp),
  color: Color = MaterialTheme.colorScheme.primary,
  strokeWidth: Dp = 2.dp,
) {
  val angle = remember { mutableFloatStateOf(0f) }
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val reduceMotion = rememberKaiReducedMotion()
  val description = stringResource(R.string.loading)
  LaunchedEffect(lifecycle, reduceMotion) {
    if (!reduceMotion)
      lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        var previous = withFrameNanos { it }
        while (isActive) {
          withFrameNanos { now ->
            angle.floatValue =
              (angle.floatValue +
                ((now - previous).coerceAtLeast(0L) / 1_000_000_000.0 * 150.0).toFloat()) % 360f
            previous = now
          }
        }
      }
  }
  Box(modifier.progressSemantics().semantics { contentDescription = description }) {
    Box(
      Modifier.fillMaxSize()
        .graphicsLayer { rotationZ = if (reduceMotion) 0f else angle.floatValue }
        .drawWithCache {
          val path = Path()
          val spiralCenter = Offset(size.width / 2f, size.height / 2f)
          val radius = size.minDimension * .42f
          repeat(181) { index ->
            val t = index / 180f
            val theta = -PI.toFloat() / 2f + t * PI.toFloat() * 4.5f
            val r = radius * (.12f + .88f * t)
            val p = spiralCenter + Offset(cos(theta) * r, sin(theta) * r)
            if (index == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
          }
          val width =
            strokeWidth
              .toPx()
              .coerceIn(.8.dp.toPx(), (size.minDimension * .13f).coerceAtLeast(.8.dp.toPx()))
          val stroke = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round)
          val depth = Stroke(width + 1.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
          val ink =
            Brush.sweepGradient(
              listOf(
                color.copy(alpha = .4f),
                color,
                color.copy(alpha = .65f),
                color.copy(alpha = .4f),
              )
            )
          onDrawBehind {
            drawPath(path, Color.Black.copy(alpha = .10f), style = depth)
            drawPath(path, ink, style = stroke)
          }
        }
    )
  }
}
