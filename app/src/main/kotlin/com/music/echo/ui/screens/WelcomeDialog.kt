package echo.music.iad1tya.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import echo.music.iad1tya.R
import echo.music.iad1tya.branding.KaiBrandHeader
import echo.music.iad1tya.branding.KaiCommunityLinks
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing

@Composable
fun WelcomeDialog(onDismissRequest: () -> Unit) {
  val maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.90f
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier =
        Modifier.padding(KaiSpacing.lg)
          .widthIn(max = 560.dp)
          .fillMaxWidth()
          .heightIn(max = maxHeight),
      shape = KaiShapes.card,
      color = MaterialTheme.colorScheme.background,
    ) {
      Column(
        Modifier.verticalScroll(rememberScrollState()).padding(KaiSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KaiSpacing.lg),
      ) {
        KaiBrandHeader()
        Text(
          stringResource(R.string.kai_welcome_description),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
        )
        KaiCommunityLinks()
        Button(
          onClick = onDismissRequest,
          modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
          shape = KaiShapes.pill,
        ) {
          Text(
            stringResource(R.string.kai_welcome_continue),
            style = MaterialTheme.typography.titleSmall,
          )
        }
      }
    }
  }
}
