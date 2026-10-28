/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.branding.KaiProject
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.Material3SettingsGroup
import echo.music.iad1tya.ui.component.Material3SettingsItem
import echo.music.iad1tya.ui.utils.backToMain

/** KAI-only checks, channel choice and explicit verified download/install actions. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSettings(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  highlightKey: String? = null,
) {
  val scrollState = rememberScrollState()
  Column(
    Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
      )
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(
      Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top))
    )
    Spacer(Modifier.height(16.dp))
    echo.music.iad1tya.updates.KaiUpdateSettingsContent()
    Spacer(
      Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
      )
    )
  }
  TopAppBar(
    title = { Text(stringResource(R.string.update_settings_title)) },
    navigationIcon = {
      IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
        Icon(
          painterResource(R.drawable.arrow_back),
          contentDescription = stringResource(R.string.kai_about_back),
        )
      }
    },
    scrollBehavior = scrollBehavior,
  )
}
