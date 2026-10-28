/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.kaiPressFeedback
import echo.music.iad1tya.ui.utils.scrollToOnHighlight

/** One calm grouped surface; rows retain their existing actions and preference controls. */
@Composable
fun Material3SettingsGroup(
  title: String? = null,
  compact: Boolean = false,
  scrollState: ScrollState? = null,
  items: List<Material3SettingsItem>,
) {
  if (items.isEmpty()) return
  Column(Modifier.fillMaxWidth()) {
    title?.let {
      PreferenceGroupTitle(it, Modifier.padding(start = KaiSpacing.xs, bottom = KaiSpacing.xs))
    }
    Column(
      Modifier.fillMaxWidth()
        .clip(KaiShapes.card)
        .background(MaterialTheme.colorScheme.surfaceContainerLow)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f), KaiShapes.card)
    ) {
      items.forEachIndexed { index, item ->
        Material3SettingsItemRow(item, compact, scrollState)
        if (index != items.lastIndex)
          HorizontalDivider(
            Modifier.padding(
              start = if (item.icon != null || item.customIcon != null) 72.dp else KaiSpacing.md
            ),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            thickness = 0.5.dp,
          )
      }
    }
  }
}

@Composable
private fun Material3SettingsItemRow(
  item: Material3SettingsItem,
  compact: Boolean = false,
  scrollState: ScrollState? = null,
) {
  val interaction = remember { MutableInteractionSource() }
  val enabled = item.enabled && item.onClick != null
  val iconSize = if (compact) 34.dp else 38.dp
  Row(
    Modifier.fillMaxWidth()
      .heightIn(min = 64.dp)
      .background(
        if (item.isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        else androidx.compose.ui.graphics.Color.Transparent
      )
      .kaiPressFeedback(interaction, enabled)
      .clickable(interactionSource = interaction, indication = null, enabled = enabled) {
        item.onClick?.invoke()
      }
      .then(
        if (scrollState != null) Modifier.scrollToOnHighlight(scrollState, item.isHighlighted)
        else Modifier
      )
      .padding(
        horizontal = KaiSpacing.md,
        vertical = if (compact) KaiSpacing.sm else KaiSpacing.md,
      ),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (item.customIcon != null || item.icon != null) {
      Box(
        Modifier.size(iconSize)
          .clip(item.iconShape ?: RoundedCornerShape(12.dp))
          .background(
            MaterialTheme.colorScheme.primary.copy(alpha = if (item.enabled) 0.08f else 0.03f)
          ),
        contentAlignment = Alignment.Center,
      ) {
        if (item.customIcon != null) item.customIcon.invoke()
        else
          item.icon?.let { painter ->
            val image: @Composable () -> Unit = {
              if (item.tintIcon)
                Icon(
                  painter,
                  contentDescription = null,
                  tint =
                    if (item.enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                  modifier = Modifier.size(22.dp),
                )
              else
                Image(
                  painter,
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.size(iconSize),
                )
            }
            if (item.showBadge)
              BadgedBox(badge = { Badge(containerColor = MaterialTheme.colorScheme.error) }) {
                image()
              }
            else image()
          }
      }
      Spacer(Modifier.width(KaiSpacing.sm))
    }
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(KaiSpacing.xxs)) {
      ProvideTextStyle(
        MaterialTheme.typography.titleMedium.copy(
          color =
            if (item.enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
      ) {
        item.title()
      }
      item.description?.let { description ->
        ProvideTextStyle(
          MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        ) {
          description()
        }
      }
    }
    item.trailingContent?.let { trailing ->
      Spacer(Modifier.width(KaiSpacing.xs))
      trailing()
    }
    if (enabled && item.trailingContent == null) {
      Spacer(Modifier.width(KaiSpacing.xs))
      Icon(
        painterResource(R.drawable.chevron_right_px),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
        modifier = Modifier.size(18.dp),
      )
    }
  }
}

data class Material3SettingsItem(
  val icon: Painter? = null,
  val customIcon: (@Composable () -> Unit)? = null,
  val title: @Composable () -> Unit,
  val description: (@Composable () -> Unit)? = null,
  val trailingContent: (@Composable () -> Unit)? = null,
  val showBadge: Boolean = false,
  val isHighlighted: Boolean = false,
  val tintIcon: Boolean = true,
  val iconShape: Shape? = null,
  val enabled: Boolean = true,
  val groupKey: String? = null,
  val onClick: (() -> Unit)? = null,
)
