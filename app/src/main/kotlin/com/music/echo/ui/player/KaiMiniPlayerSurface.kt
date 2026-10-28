/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.ui.component.GlassComponent
import echo.music.iad1tya.ui.component.GlassEffectConfig
import echo.music.iad1tya.ui.component.forKaiOverlay
import echo.music.iad1tya.ui.component.liquidGlass
import echo.music.iad1tya.ui.theme.KaiShapes

/** A single frosted backdrop pass, with the same quiet edge on the opaque fallback. */
@Composable
fun Modifier.kaiMiniPlayerSurface(
  useGlass: Boolean,
  config: GlassEffectConfig,
  backgroundColor: Color,
  shape: CornerBasedShape = KaiShapes.card,
): Modifier {
  val edge = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
  return shadow(
      elevation = if (useGlass) 0.dp else 6.dp,
      shape = shape,
      clip = false,
      ambientColor = Color.Black.copy(alpha = 0.10f),
      spotColor = Color.Black.copy(alpha = 0.16f),
    )
    .clip(shape)
    .then(
      if (useGlass) Modifier.liquidGlass(config.forKaiOverlay(GlassComponent.MINI_PLAYER), shape)
      else Modifier.background(backgroundColor)
    )
    .border(1.dp, edge, shape)
    .drawWithCache {
      val highlight =
        Brush.linearGradient(
          listOf(Color.White.copy(alpha = 0.10f), Color.Transparent),
          Offset.Zero,
          Offset(0f, size.height * 0.7f),
        )
      onDrawWithContent {
        drawRect(highlight)
        drawContent()
      }
    }
}
