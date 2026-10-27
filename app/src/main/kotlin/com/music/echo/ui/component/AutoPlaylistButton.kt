package echo.music.iad1tya.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.kaiPressFeedback

@Composable
fun AutoPlaylistButton(
  title: String,
  icon: Int,
  iconTint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val interaction = remember { MutableInteractionSource() }
  Surface(
    shape = KaiShapes.artwork,
    color = Color.Transparent,
    modifier =
      modifier
        .defaultMinSize(minHeight = 56.dp)
        .kaiPressFeedback(interaction)
        .clickable(interactionSource = interaction, indication = null, onClick = onClick),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = KaiSpacing.sm, vertical = KaiSpacing.sm),
    ) {
      Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
      )
    }
  }
}
