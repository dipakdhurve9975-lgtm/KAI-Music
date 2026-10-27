package echo.music.iad1tya.ui.player

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.KaiMotion
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion

/** Icon changes share a short fade while transport state and semantics update immediately. */
@Composable
internal fun KaiTransportGlyph(
  @DrawableRes icon: Int,
  description: String,
  modifier: Modifier = Modifier,
) {
  val reducedMotion = rememberKaiReducedMotion()
  Crossfade(
    icon,
    modifier,
    animationSpec = tween(if (reducedMotion) 0 else KaiMotion.feedbackMillis),
    label = "kaiTransport",
  ) { target ->
    Icon(painterResource(target), description, modifier = Modifier.fillMaxSize())
  }
}

/** Presentation only: callbacks route to the existing sheet and player menu. */
@Composable
fun KaiNowPlayingHeader(
  source: String?,
  color: Color,
  onCollapse: () -> Unit,
  onMore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = KaiSpacing.sm),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(onClick = onCollapse, modifier = Modifier.size(KaiSpacing.minimumTouchTarget)) {
      Icon(painterResource(R.drawable.expand_more), stringResource(R.string.close), tint = color)
    }
    Column(
      Modifier.weight(1f).padding(horizontal = KaiSpacing.xs),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        stringResource(R.string.now_playing),
        style = MaterialTheme.typography.labelLarge,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      if (!source.isNullOrBlank())
        Text(
          source,
          style = MaterialTheme.typography.bodySmall,
          color = color.copy(alpha = 0.65f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
    }
    IconButton(onClick = onMore, modifier = Modifier.size(KaiSpacing.minimumTouchTarget)) {
      Icon(
        painterResource(R.drawable.more_horiz),
        stringResource(R.string.more_options),
        tint = color,
      )
    }
  }
}
