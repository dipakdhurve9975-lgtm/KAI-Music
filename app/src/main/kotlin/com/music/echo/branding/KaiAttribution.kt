// Modified for KAI Music on 2026-10-28; see MODIFICATIONS.md. GPL-3.0.
package echo.music.iad1tya.branding

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Local attribution: About never starts a contributors/network request. */
@Composable
fun KaiOrigins() {
  AttributionSection(stringResource(R.string.kai_origins_heading)) {
    Text(stringResource(R.string.kai_origins_title), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.kai_origins_summary), style = MaterialTheme.typography.bodyMedium)
    ExpandableAcknowledgment(stringResource(R.string.kai_origins_detail))
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KaiOriginalCreator() {
  AttributionSection(stringResource(R.string.kai_original_thanks_heading), emphasized = true) {
    Text(
      stringResource(R.string.kai_original_thanks_title),
      style = MaterialTheme.typography.titleLarge,
    )
    Text("Aditya · @iad1tya", style = MaterialTheme.typography.titleMedium)
    Text(
      stringResource(R.string.kai_original_thanks_summary),
      style = MaterialTheme.typography.bodyMedium,
    )
    ExpandableAcknowledgment(stringResource(R.string.kai_original_thanks_detail))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(KaiSpacing.xs)) {
      ProjectLink(stringResource(R.string.kai_original_github), KaiProject.ORIGINAL_CREATOR)
      ProjectLink(stringResource(R.string.kai_original_repository), KaiProject.ORIGINAL_SOURCE)
      ProjectLink(stringResource(R.string.kai_view_contributors), KaiProject.ORIGINAL_CONTRIBUTORS)
    }
  }
}

@Composable
fun KaiIndependentDevelopment() {
  AttributionSection(stringResource(R.string.kai_development_heading)) {
    Text(
      stringResource(R.string.kai_development_title),
      style = MaterialTheme.typography.titleLarge,
    )
    Text(
      stringResource(R.string.kai_development_description),
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KaiSourceLicense() {
  var document by rememberSaveable { mutableStateOf<String?>(null) }
  AttributionSection(stringResource(R.string.kai_license_heading)) {
    Text(stringResource(R.string.kai_license_title), style = MaterialTheme.typography.titleMedium)
    Text(
      stringResource(R.string.kai_license_description),
      style = MaterialTheme.typography.bodyMedium,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(KaiSpacing.xs)) {
      TextButton(onClick = { document = "GPL-3.0.txt" }) {
        Text(stringResource(R.string.kai_view_license))
      }
      ProjectLink(stringResource(R.string.kai_view_source), KaiProject.SOURCE)
      ProjectLink(stringResource(R.string.kai_original_project), KaiProject.ORIGINAL_SOURCE)
      TextButton(onClick = { document = "THIRD_PARTY_NOTICES.txt" }) {
        Text(stringResource(R.string.kai_bundled_notices))
      }
    }
    Text(
      stringResource(R.string.kai_independence_disclaimer),
      style = MaterialTheme.typography.bodySmall,
    )
  }
  document?.let { filename -> LocalLicenseDialog(filename) { document = null } }
}

@Composable
private fun AttributionSection(
  title: String,
  emphasized: Boolean = false,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm)) {
    Text(
      title,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Surface(
      shape = KaiShapes.card,
      color =
        if (emphasized) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
        else MaterialTheme.colorScheme.surfaceContainerLow,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(
        Modifier.fillMaxWidth().padding(KaiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm),
        content = content,
      )
    }
  }
}

@Composable
private fun ExpandableAcknowledgment(detail: String) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  if (expanded) Text(detail, style = MaterialTheme.typography.bodyMedium)
  TextButton(onClick = { expanded = !expanded }) {
    Text(stringResource(if (expanded) R.string.kai_read_less else R.string.kai_read_more))
  }
}

@Composable
private fun ProjectLink(label: String, destination: String) {
  val handler = LocalUriHandler.current
  val context = LocalContext.current
  val failure = stringResource(R.string.kai_link_unavailable)
  TextButton(
    onClick = {
      runCatching { handler.openUri(destination) }
        .onFailure { Toast.makeText(context, failure, Toast.LENGTH_SHORT).show() }
    }
  ) {
    Text(label)
  }
}

@Composable
private fun LocalLicenseDialog(filename: String, onDismiss: () -> Unit) {
  val context = LocalContext.current
  var paragraphs by remember(filename) { mutableStateOf<List<String>>(emptyList()) }
  val unavailable = stringResource(R.string.kai_license_unavailable)
  LaunchedEffect(filename) {
    paragraphs =
      withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open("licenses/$filename").bufferedReader().use { it.readText() }
          }
          .getOrDefault(unavailable)
          .split("\n\n")
          .flatMap { it.chunked(2000) }
      }
  }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        if (filename == "GPL-3.0.txt") stringResource(R.string.kai_license_title)
        else stringResource(R.string.kai_bundled_notices)
      )
    },
    text = {
      LazyColumn(
        Modifier.heightIn(max = 440.dp),
        verticalArrangement = Arrangement.spacedBy(KaiSpacing.sm),
      ) {
        items(paragraphs) { paragraph ->
          Text(paragraph, style = MaterialTheme.typography.bodySmall)
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(R.string.kai_license_close)) }
    },
  )
}
