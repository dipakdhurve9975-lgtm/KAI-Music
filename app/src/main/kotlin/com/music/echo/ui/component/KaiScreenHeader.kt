package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.ui.theme.KaiSpacing

/** Shared editorial hierarchy, with intrinsic height for accessibility font scaling. */
@Composable
fun KaiScreenHeader(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  horizontalPadding: Dp = KaiSpacing.page,
) {
  Column(
    modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = KaiSpacing.lg),
    verticalArrangement = Arrangement.spacedBy(KaiSpacing.xs),
  ) {
    Text(
      title,
      style = MaterialTheme.typography.headlineLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.semantics { heading() },
    )
    subtitle
      ?.takeIf { it.isNotBlank() }
      ?.let {
        Text(
          it,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
  }
}

@Composable
fun KaiEmptyState(icon: Int, title: String, description: String, modifier: Modifier = Modifier) {
  Column(
    modifier.fillMaxWidth().padding(horizontal = KaiSpacing.xl, vertical = KaiSpacing.xxl),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm),
  ) {
    Box(
      Modifier.size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        painterResource(icon),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(28.dp),
      )
    }
    Text(
      title,
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
    )
    Text(
      description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
  }
}
