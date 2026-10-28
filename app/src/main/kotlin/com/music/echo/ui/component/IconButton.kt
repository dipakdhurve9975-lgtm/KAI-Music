/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.kaiPressFeedback

@Composable
fun ResizableIconButton(
  @DrawableRes icon: Int,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.onSurface,
  enabled: Boolean = true,
  indication: Indication? = null,
  contentDescription: String? = null,
  onClick: () -> Unit = {},
) {
  val interactionSource = remember { MutableInteractionSource() }
  val description =
    contentDescription
      ?: when (icon) {
        R.drawable.skip_previous,
        R.drawable.apple_skip_previous -> stringResource(R.string.previous)
        R.drawable.skip_next,
        R.drawable.apple_skip_next -> stringResource(R.string.next)
        R.drawable.play,
        R.drawable.play_applemusic -> stringResource(R.string.play)
        R.drawable.pause,
        R.drawable.pause_applemusic -> stringResource(R.string.pause)
        else -> null
      }
  Image(
    painter = painterResource(icon),
    contentDescription = description,
    colorFilter = ColorFilter.tint(color),
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .kaiPressFeedback(interactionSource, enabled)
        .clickable(
          indication = indication ?: ripple(bounded = false),
          interactionSource = interactionSource,
          role = Role.Button,
          enabled = enabled,
          onClick = onClick,
        )
        .alpha(if (enabled) 1f else 0.5f),
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconButton(
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable () -> Unit,
) {
  Box(
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        .clip(CircleShape)
        .background(color = colors.containerColor)
        .combinedClickable(
          onClick = onClick,
          onLongClick = onLongClick,
          enabled = enabled,
          role = Role.Button,
          interactionSource = interactionSource,
          indication = ripple(bounded = false, radius = 24.dp),
        ),
    contentAlignment = Alignment.Center,
  ) {
    val contentColor = colors.contentColor
    CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
  }
}
