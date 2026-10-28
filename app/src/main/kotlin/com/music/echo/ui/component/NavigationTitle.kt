/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing

@Composable
fun NavigationTitle(
  title: String,
  modifier: Modifier = Modifier,
  label: String? = null,
  thumbnail: (@Composable () -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  onPlayAllClick: (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier =
      modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
        .clickable(enabled = onClick != null) { onClick?.invoke() }
        .padding(horizontal = KaiSpacing.page, vertical = KaiSpacing.md),
  ) {
    thumbnail?.invoke()

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
      label?.let { label ->
        Text(
          text = label,
          style = MaterialTheme.typography.labelLarge,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
      )
    }

    onPlayAllClick?.let { playAllClick ->
      OutlinedButton(
        onClick = playAllClick,
        shape = KaiShapes.pill,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
        colors =
          ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        modifier = Modifier.heightIn(min = 48.dp),
      ) {
        Text(text = stringResource(R.string.play_all), style = MaterialTheme.typography.labelSmall)
      }
    }

    if (onClick != null) {
      Icon(
        painter = painterResource(R.drawable.arrow_forward),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
