/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.constants.ThumbnailCornerRadius

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RandomizeGridItem(isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

  val dotOffsetMultiplier by
    animateFloatAsState(
      targetValue = if (isLoading) 0f else 1f,
      animationSpec = tween(durationMillis = 600),
      label = "dotOffset",
    )

  Box(
    modifier =
      modifier
        .aspectRatio(1f)
        .clip(RoundedCornerShape(ThumbnailCornerRadius))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    val dotColor = MaterialTheme.colorScheme.onSurface
    val dotSize = 14.dp
    val padding = 24.dp

    Box(
      modifier =
        Modifier.align(Alignment.Center)
          .offset(x = -padding * dotOffsetMultiplier, y = -padding * dotOffsetMultiplier)
          .size(dotSize)
          .clip(CircleShape)
          .background(dotColor)
    )

    Box(
      modifier =
        Modifier.align(Alignment.Center)
          .offset(x = padding * dotOffsetMultiplier, y = -padding * dotOffsetMultiplier)
          .size(dotSize)
          .clip(CircleShape)
          .background(dotColor)
    )

    Box(
      modifier =
        Modifier.align(Alignment.Center).size(dotSize).clip(CircleShape).background(dotColor)
    )

    Box(
      modifier =
        Modifier.align(Alignment.Center)
          .offset(x = -padding * dotOffsetMultiplier, y = padding * dotOffsetMultiplier)
          .size(dotSize)
          .clip(CircleShape)
          .background(dotColor)
    )

    Box(
      modifier =
        Modifier.align(Alignment.Center)
          .offset(x = padding * dotOffsetMultiplier, y = padding * dotOffsetMultiplier)
          .size(dotSize)
          .clip(CircleShape)
          .background(dotColor)
    )

    if (isLoading) {
      KaiSpiralLoading(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}
