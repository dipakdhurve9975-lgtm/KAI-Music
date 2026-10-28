/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.branding

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.kaiPressFeedback

/** Shared by About and onboarding so no previous account or destination can leak through. */
@Composable
fun KaiBrandHeader(modifier: Modifier = Modifier, tagline: String? = null) {
  val channel =
    stringResource(
      when {
        BuildConfig.DEBUG -> R.string.kai_channel_development
        BuildConfig.IS_NIGHTLY -> R.string.kai_channel_nightly
        else -> R.string.kai_channel_stable
      }
    )
  Column(
    modifier = modifier.fillMaxWidth().padding(vertical = KaiSpacing.lg),
    verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(shape = KaiShapes.card, color = Color(0xFF260E19)) {
      Image(
        painter = painterResource(R.drawable.kai_mark),
        contentDescription = null,
        modifier = Modifier.size(88.dp),
      )
    }
    Text(
      "KAI",
      style = MaterialTheme.typography.displaySmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
      tagline ?: stringResource(R.string.kai_about_tagline),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
    Text(
      stringResource(R.string.kai_about_version, BuildConfig.VERSION_NAME, channel),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun KaiCommunityLinks(
  modifier: Modifier = Modifier,
  configuration: KaiLinkConfiguration = KaiLinks.configuration,
) {
  Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(KaiSpacing.lg)) {
    KaiLinkGroup(
      R.string.kai_about_developer,
      listOf(KaiLink.WEBSITE, KaiLink.INSTAGRAM, KaiLink.INSTAGRAM_FRIEND),
      configuration,
    )
    KaiLinkGroup(R.string.kai_about_support, listOf(KaiLink.COFFEE, KaiLink.UPI), configuration)
    KaiLinkGroup(
      R.string.kai_about_community,
      listOf(KaiLink.GITHUB, KaiLink.DISCORD, KaiLink.TELEGRAM),
      configuration,
    )
  }
}

@Composable
fun KaiLegalNotice(modifier: Modifier = Modifier) {
  Column(
    modifier.fillMaxWidth().padding(vertical = KaiSpacing.sm),
    verticalArrangement = Arrangement.spacedBy(KaiSpacing.xs),
  ) {
    Text(
      stringResource(R.string.kai_about_legal_title),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
      stringResource(R.string.kai_about_legal_description),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun KaiLinkGroup(
  @StringRes title: Int,
  links: List<KaiLink>,
  configuration: KaiLinkConfiguration,
) {
  Column(verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm)) {
    Text(
      stringResource(title),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(start = KaiSpacing.xs),
    )
    Column(
      Modifier.fillMaxWidth()
        .clip(KaiShapes.card)
        .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
      links.forEachIndexed { index, link ->
        androidx.compose.runtime.key(link) {
          KaiLinkRow(link, configuration)
          if (index < links.lastIndex)
            HorizontalDivider(
              modifier = Modifier.padding(start = 68.dp, end = KaiSpacing.md),
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            )
        }
      }
    }
  }
}

@Composable
private fun KaiLinkRow(link: KaiLink, configuration: KaiLinkConfiguration) {
  val destination = configuration.destination(link)
  val configured = destination != null
  val uriHandler = LocalUriHandler.current
  val context = LocalContext.current
  val unavailableMessage = stringResource(R.string.kai_link_unavailable)
  val interactionSource = remember { MutableInteractionSource() }
  val title = stringResource(link.title)
  Row(
    Modifier.fillMaxWidth()
      .heightIn(min = 72.dp)
      .kaiPressFeedback(interactionSource, enabled = configured)
      .clickable(
        enabled = configured,
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
      ) {
        // Validate again at the action boundary; empty configuration never launches anything.
        configuration.destination(link)?.let { target ->
          runCatching { uriHandler.openUri(target) }
            .onFailure {
              Toast.makeText(context, unavailableMessage, Toast.LENGTH_SHORT).show()
            }
        }
      }
      .padding(horizontal = KaiSpacing.md, vertical = KaiSpacing.sm),
    horizontalArrangement = Arrangement.spacedBy(KaiSpacing.md),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
      Icon(
        painterResource(link.icon),
        contentDescription = null,
        modifier = Modifier.size(22.dp),
        tint = MaterialTheme.colorScheme.primary.copy(alpha = if (configured) 1f else 0.6f),
      )
    }
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(KaiSpacing.xxs)) {
      Text(
        title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        configuration.displayValue(link) ?: stringResource(R.string.kai_link_coming_soon),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
    if (configured)
      Icon(
        painterResource(R.drawable.ic_nav_chevron_right),
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
  }
}

private val KaiLink.title: Int
  @StringRes
  get() =
    when (this) {
      KaiLink.WEBSITE -> R.string.kai_link_website
      KaiLink.INSTAGRAM,
      KaiLink.INSTAGRAM_FRIEND -> R.string.kai_link_instagram
      KaiLink.GITHUB -> R.string.kai_link_github
      KaiLink.DISCORD -> R.string.kai_link_discord
      KaiLink.COFFEE -> R.string.kai_link_coffee
      KaiLink.UPI -> R.string.kai_link_upi
      KaiLink.TELEGRAM -> R.string.kai_link_telegram
    }

private val KaiLink.icon: Int
  @DrawableRes
  get() =
    when (this) {
      KaiLink.WEBSITE -> R.drawable.website
      KaiLink.INSTAGRAM,
      KaiLink.INSTAGRAM_FRIEND -> R.drawable.ic_instagram_new
      KaiLink.GITHUB -> R.drawable.github
      KaiLink.DISCORD -> R.drawable.ic_discord_new
      KaiLink.COFFEE -> R.drawable.coffee
      KaiLink.UPI -> R.drawable.upi_new
      KaiLink.TELEGRAM -> R.drawable.ic_telegram_new
    }
