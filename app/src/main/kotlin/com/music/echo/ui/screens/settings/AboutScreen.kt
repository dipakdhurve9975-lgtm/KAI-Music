/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.branding.KaiBrandHeader
import echo.music.iad1tya.branding.KaiCommunityLinks
import echo.music.iad1tya.branding.KaiIndependentDevelopment
import echo.music.iad1tya.branding.KaiOriginalCreator
import echo.music.iad1tya.branding.KaiOrigins
import echo.music.iad1tya.branding.KaiSourceLicense
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.utils.backToMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  onBack: (() -> Unit)? = null,
  highlightKey: String? = null,
) {
  val playerInsets = LocalPlayerAwareWindowInsets.current
  Scaffold(
    modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            stringResource(R.string.about),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { onBack?.invoke() ?: navController.navigateUp() },
            onLongClick = navController::backToMain,
          ) {
            Icon(
              painterResource(R.drawable.arrow_back),
              contentDescription = stringResource(R.string.kai_about_back),
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
          ),
        scrollBehavior = scrollBehavior,
      )
    },
  ) { innerPadding ->
    LazyColumn(
      modifier =
        Modifier.fillMaxSize().windowInsetsPadding(playerInsets.only(WindowInsetsSides.Horizontal)),
      contentPadding =
        PaddingValues(
          start = KaiSpacing.page,
          end = KaiSpacing.page,
          top = innerPadding.calculateTopPadding() + KaiSpacing.xs,
          bottom = playerInsets.asPaddingValues().calculateBottomPadding() + KaiSpacing.lg,
        ),
      verticalArrangement = Arrangement.spacedBy(KaiSpacing.lg),
    ) {
      item(key = "brand") {
        KaiBrandHeader(tagline = stringResource(R.string.kai_independent_tagline))
      }
      item(key = "origins") { KaiOrigins() }
      item(key = "original_creator") { KaiOriginalCreator() }
      item(key = "independent_development") { KaiIndependentDevelopment() }
      item(key = "links") { KaiCommunityLinks(includeOriginalCreatorInstagram = true) }
      item(key = "legal") { KaiSourceLicense() }
    }
  }
}
