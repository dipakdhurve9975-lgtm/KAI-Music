// Modified for KAI Music on 2026-10-29; GPL-3.0. See MODIFICATIONS.md.
package echo.music.iad1tya.updates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.EntryPointAccessors
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.KaiSpiralLoading
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import kotlinx.coroutines.awaitCancellation

@Composable
internal fun rememberKaiUpdates(): KaiUpdateManager {
  val context = LocalContext.current.applicationContext
  return remember(context) {
    EntryPointAccessors.fromApplication(context, KaiUpdateEntryPoint::class.java).updates()
  }
}

/** One host above navigation. Checks never block playback or screen creation. */
@Composable
internal fun KaiUpdateHost() {
  val manager = rememberKaiUpdates()
  val state by manager.state.collectAsStateWithLifecycle()
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  LaunchedEffect(manager, lifecycle) {
    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
      manager.check()
      awaitCancellation()
    }
  }
  if (state.showDialog && state.offer != null) KaiUpdateDialog(manager, state)
}

@Composable
internal fun KaiUpdateSettingsContent() {
  val manager = rememberKaiUpdates()
  val state by manager.state.collectAsStateWithLifecycle()
  val uri = LocalUriHandler.current
  val context = LocalContext.current
  val busy = state.transfer in setOf(UpdateTransfer.DOWNLOADING, UpdateTransfer.VERIFYING)
  Column(verticalArrangement = Arrangement.spacedBy(KaiSpacing.md)) {
    Text(
      stringResource(R.string.kai_updates_current, BuildConfig.VERSION_NAME),
      style = MaterialTheme.typography.headlineSmall,
    )
    Text(
      stringResource(R.string.kai_updates_identity),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Button(
      onClick = { manager.check(manual = true) },
      enabled = state.check != UpdateCheck.CHECKING && !busy,
    ) {
      Text(stringResource(R.string.kai_updates_check))
    }
    if (state.check == UpdateCheck.CHECKING) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        KaiSpiralLoading(Modifier.size(20.dp))
        Text(stringResource(R.string.kai_updates_checking))
      }
    }
    if (state.check == UpdateCheck.CURRENT)
      Text(
        stringResource(R.string.kai_updates_current_result),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    if (state.check == UpdateCheck.ERROR)
      Text(updateError(state.failure), color = MaterialTheme.colorScheme.error)
    state.offer?.let { offer ->
      OutlinedButton(onClick = manager::showOffer) {
        Text(stringResource(R.string.kai_updates_available, offer.manifest.versionName))
      }
    }
    if (state.lastSuccess > 0L) {
      Text(
        stringResource(
          R.string.kai_updates_last_check,
          java.text.DateFormat.getDateTimeInstance(
              java.text.DateFormat.MEDIUM,
              java.text.DateFormat.SHORT,
            )
            .format(java.util.Date(state.lastSuccess)),
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .4f))
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(Modifier.weight(1f)) {
        Text(
          stringResource(R.string.kai_updates_preview),
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          stringResource(R.string.kai_updates_preview_description),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Switch(
        checked = state.includePreview,
        onCheckedChange = manager::setPreview,
        enabled = !busy && state.check != UpdateCheck.CHECKING,
      )
    }
    Text(
      stringResource(R.string.kai_updates_install_explanation),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      TextButton(
        onClick = { openUpdateUrl(context, uri, state.offer?.release?.url ?: KAI_RELEASES) }
      ) {
        Text(stringResource(R.string.kai_updates_notes))
      }
      TextButton(onClick = { openUpdateUrl(context, uri, KAI_RELEASES) }) {
        Text(stringResource(R.string.kai_updates_github))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KaiUpdateDialog(manager: KaiUpdateManager, state: KaiUpdateState) {
  val offer = state.offer ?: return
  val busy = state.transfer in setOf(UpdateTransfer.DOWNLOADING, UpdateTransfer.VERIFYING)
  val context = LocalContext.current
  val uri = LocalUriHandler.current
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  var canInstall by
    remember(context) { mutableStateOf(context.packageManager.canRequestPackageInstalls()) }
  LaunchedEffect(context, lifecycle) {
    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
      canInstall = context.packageManager.canRequestPackageInstalls()
      awaitCancellation()
    }
  }
  BasicAlertDialog(onDismissRequest = manager::dismiss) {
    Surface(shape = KaiShapes.card, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
      Column(
        Modifier.padding(24.dp).heightIn(max = 620.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          stringResource(R.string.kai_updates_available, offer.manifest.versionName),
          style = MaterialTheme.typography.headlineSmall,
        )
        Text(
          stringResource(R.string.kai_updates_current, BuildConfig.VERSION_NAME),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (offer.release.prerelease)
          Text(
            stringResource(R.string.kai_updates_preview_warning),
            style = MaterialTheme.typography.labelLarge,
          )
        Text(
          stringResource(R.string.kai_updates_notes),
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          offer.release.body?.takeIf { it.isNotBlank() }?.take(24_000)
            ?: stringResource(R.string.kai_updates_no_notes),
          modifier = Modifier.heightIn(max = 230.dp).verticalScroll(rememberScrollState()),
          style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = { openUpdateUrl(context, uri, offer.release.url) }) {
          Text(stringResource(R.string.kai_updates_full_notes))
        }
        when (state.transfer) {
          UpdateTransfer.DOWNLOADING -> {
            val fraction = (state.downloaded.toDouble() / offer.apk.size).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())
            Text(
              stringResource(
                R.string.kai_updates_progress,
                (fraction * 100).toInt(),
                "%.1f".format(state.downloaded / 1048576.0),
                "%.1f".format(offer.apk.size / 1048576.0),
              )
            )
          }
          UpdateTransfer.VERIFYING -> {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              KaiSpiralLoading(Modifier.size(20.dp))
              Text(stringResource(R.string.kai_updates_verifying))
            }
          }
          UpdateTransfer.READY -> Text(stringResource(R.string.kai_updates_ready))
          UpdateTransfer.ERROR ->
            Text(updateError(state.failure), color = MaterialTheme.colorScheme.error)
          else ->
            Text(
              stringResource(R.string.kai_updates_size, "%.1f".format(offer.apk.size / 1048576.0)),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (state.transfer == UpdateTransfer.READY)
          Text(
            stringResource(R.string.kai_updates_install_explanation),
            style = MaterialTheme.typography.bodySmall,
          )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = if (busy) manager::cancelDownload else manager::dismiss) {
            Text(
              stringResource(if (busy) R.string.kai_updates_cancel else R.string.kai_updates_later)
            )
          }
          if (!busy)
            Button(
              onClick =
                if (state.transfer == UpdateTransfer.READY) manager::install else manager::download
            ) {
              Text(
                stringResource(
                  when {
                    state.transfer == UpdateTransfer.READY && !canInstall ->
                      R.string.kai_updates_allow_install
                    state.transfer == UpdateTransfer.READY -> R.string.kai_updates_install
                    state.transfer == UpdateTransfer.ERROR -> R.string.kai_updates_retry
                    else -> R.string.kai_updates_download
                  }
                )
              )
            }
        }
      }
    }
  }
}

private fun openUpdateUrl(
  context: android.content.Context,
  uri: androidx.compose.ui.platform.UriHandler,
  url: String,
) {
  runCatching { uri.openUri(url) }
    .onFailure {
      android.widget.Toast.makeText(
          context,
          R.string.kai_link_unavailable,
          android.widget.Toast.LENGTH_SHORT,
        )
        .show()
    }
}

@Composable
private fun updateError(reason: KaiUpdateFailure.Reason?): String =
  stringResource(
    when (reason) {
      KaiUpdateFailure.Reason.RATE_LIMIT -> R.string.kai_updates_error_rate
      KaiUpdateFailure.Reason.METADATA -> R.string.kai_updates_error_metadata
      KaiUpdateFailure.Reason.VERIFY -> R.string.kai_updates_error_verify
      KaiUpdateFailure.Reason.INSTALL -> R.string.kai_updates_error_install
      KaiUpdateFailure.Reason.DOWNLOAD -> R.string.kai_updates_error_download
      else -> R.string.kai_updates_error_network
    }
  )
