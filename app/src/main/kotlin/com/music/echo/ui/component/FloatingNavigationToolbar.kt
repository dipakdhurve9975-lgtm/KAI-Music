package echo.music.iad1tya.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import kotlinx.coroutines.launch

/** Fixed cells and one draggable lens, controlled by the actual navigation destination. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingNavigationToolbar(
  items: List<Screens>,
  pureBlack: Boolean,
  modifier: Modifier = Modifier,
  onFabClick: (() -> Unit)? = null,
  fabIconRes: Int? = null,
  fabContentDescription: String = "",
  onMusicFlowClick: (() -> Unit)? = null,
  musicFlowIconRes: Int? = R.drawable.swipe,
  musicFlowContentDescription: String = "",
  onShuffleClick: (() -> Unit)? = null,
  shuffleEnabled: Boolean = false,
  shuffleIconRes: Int? = null,
  shuffleContentDescription: String = "",
  onMusicRecognitionClick: (() -> Unit)? = null,
  musicRecognitionContentDescription: String = "",
  onAiHubClick: (() -> Unit)? = null,
  aiHubIconRes: Int? = null,
  aiHubContentDescription: String = "",
  scrollBehavior: FloatingToolbarScrollBehavior? = null,
  onSearchLongClick: (() -> Unit)? = null,
  isSelected: (Screens) -> Boolean,
  onItemClick: (Screens, Boolean) -> Unit,
  currentRoute: String? = null,
) {
  // More is an overlay: its visibility is the only local UI state. No selected tab is remembered.
  var showMore by rememberSaveable { mutableStateOf(false) }
  val cells = buildList {
    items.forEach { screen ->
      if (screen == Screens.Library && onMusicRecognitionClick != null)
        add(NavigationCell.Recognition)
      add(NavigationCell.Destination(screen))
    }
    if (onMusicRecognitionClick != null && items.none { it == Screens.Library })
      add(NavigationCell.Recognition)
    if (
      onMusicFlowClick != null ||
        (onShuffleClick != null && shuffleIconRes != null) ||
        (onAiHubClick != null && aiHubIconRes != null)
    )
      add(NavigationCell.More)
    else if (onFabClick != null && fabIconRes != null) add(NavigationCell.Fab)
  }
  if (cells.isEmpty()) return
  val selectedIndex =
    cells
      .indexOfFirst { cell ->
        when (cell) {
          is NavigationCell.Destination ->
            !showMore && currentRoute != "recognition" && isSelected(cell.screen)
          NavigationCell.Recognition -> !showMore && currentRoute == "recognition"
          NavigationCell.More -> showMore
          NavigationCell.Fab -> false
        }
      }
      .coerceAtLeast(0)
  val labels = cells.map { cell ->
    when (cell) {
      is NavigationCell.Destination -> stringResource(cell.screen.titleId)
      NavigationCell.Recognition ->
        musicRecognitionContentDescription.ifEmpty { stringResource(R.string.recognition) }
      NavigationCell.More -> stringResource(R.string.more_label)
      NavigationCell.Fab ->
        fabContentDescription.ifEmpty { stringResource(R.string.create_playlist) }
    }
  }
  val glassConfig = LocalGlassEffectConfig.current
  val foreground =
    when {
      glassConfig.textColor.isSpecified -> glassConfig.textColor
      pureBlack -> Color.White
      else -> MaterialTheme.colorScheme.onSurface
    }
  val showLabels = LocalDensity.current.fontScale <= 1.3f
  val choose: (Int) -> Unit = { index ->
    when (val cell = cells[index]) {
      is NavigationCell.Destination -> onItemClick(cell.screen, isSelected(cell.screen))
      NavigationCell.Recognition -> onMusicRecognitionClick?.invoke()
      NavigationCell.More -> showMore = true
      NavigationCell.Fab -> onFabClick?.invoke()
    }
  }
  Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    KaiLiquidSegmentedControl(
      labels = labels,
      selectedIndex = selectedIndex,
      onSelectedIndexChange = choose,
      contentColor = foreground,
      modifier = Modifier.widthIn(max = 480.dp).shadow(8.dp, KaiShapes.pill),
      backdrop = LocalAppBackdrop.current,
      glassComponent = GlassComponent.NAV_BAR,
      minSegmentHeight = 64.dp,
      onSegmentReselected = choose,
    ) { index, selected ->
      val cell = cells[index]
      val icon =
        when (cell) {
          is NavigationCell.Destination ->
            if (selected) cell.screen.iconIdActive else cell.screen.iconIdInactive
          NavigationCell.Recognition -> R.drawable.mic
          NavigationCell.More -> R.drawable.more_horiz
          NavigationCell.Fab -> fabIconRes ?: R.drawable.add
        }
      // Constant geometry: no measured-position maps, expanding labels or layout feedback loops.
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
      ) {
        Icon(
          painter = painterResource(icon),
          contentDescription = null,
          tint = foreground.copy(alpha = if (selected) 1f else 0.68f),
          modifier = Modifier.size(22.dp),
        )
        if (showLabels)
          Text(
            text = labels[index],
            color = foreground.copy(alpha = if (selected) 1f else 0.68f),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clearAndSetSemantics {},
          )
      }
    }
  }
  if (showMore)
    KaiNavigationMoreSheet(
      onDismiss = { showMore = false },
      onMusicFlowClick = onMusicFlowClick,
      musicFlowIconRes = musicFlowIconRes,
      musicFlowContentDescription = musicFlowContentDescription,
      onShuffleClick = onShuffleClick,
      shuffleEnabled = shuffleEnabled,
      shuffleIconRes = shuffleIconRes,
      shuffleContentDescription = shuffleContentDescription,
      onAiHubClick = onAiHubClick,
      aiHubIconRes = aiHubIconRes,
      aiHubContentDescription = aiHubContentDescription,
    )
}

private sealed interface NavigationCell {
  data class Destination(val screen: Screens) : NavigationCell

  data object Recognition : NavigationCell

  data object More : NavigationCell

  data object Fab : NavigationCell
}

/** Existing More actions share one modal. Opening it never dispatches a page navigation event. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KaiNavigationMoreSheet(
  onDismiss: () -> Unit,
  onMusicFlowClick: (() -> Unit)?,
  musicFlowIconRes: Int?,
  musicFlowContentDescription: String,
  onShuffleClick: (() -> Unit)?,
  shuffleEnabled: Boolean,
  shuffleIconRes: Int?,
  shuffleContentDescription: String,
  onAiHubClick: (() -> Unit)?,
  aiHubIconRes: Int?,
  aiHubContentDescription: String,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  fun dismissAndRun(action: () -> Unit) {
    scope.launch {
      sheetState.hide()
      onDismiss()
      action()
    }
  }
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    dragHandle = { BottomSheetDefaults.DragHandle() },
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth().padding(horizontal = KaiSpacing.md).padding(bottom = KaiSpacing.xl),
      verticalArrangement = Arrangement.spacedBy(KaiSpacing.xxs),
    ) {
      Text(
        text = stringResource(R.string.more_options),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = KaiSpacing.xs),
      )
      Material3SettingsGroup(
        compact = true,
        items =
          buildList {
            if (onMusicFlowClick != null && musicFlowIconRes != null)
              add(
                Material3SettingsItem(
                  title = {
                    Text(
                      musicFlowContentDescription.ifEmpty { stringResource(R.string.music_flow) }
                    )
                  },
                  icon = painterResource(musicFlowIconRes),
                  onClick = { dismissAndRun(onMusicFlowClick) },
                )
              )
            if (onShuffleClick != null && shuffleIconRes != null)
              add(
                Material3SettingsItem(
                  title = {
                    Text(shuffleContentDescription.ifEmpty { stringResource(R.string.shuffle) })
                  },
                  icon = painterResource(shuffleIconRes),
                  trailingContent = {
                    Switch(checked = shuffleEnabled, onCheckedChange = { onShuffleClick() })
                  },
                  onClick = onShuffleClick,
                )
              )
            if (onAiHubClick != null && aiHubIconRes != null)
              add(
                Material3SettingsItem(
                  title = { Text(aiHubContentDescription) },
                  icon = painterResource(aiHubIconRes),
                  tintIcon = false,
                  iconShape = androidx.compose.foundation.shape.CircleShape,
                  onClick = { dismissAndRun(onAiHubClick) },
                )
              )
          },
      )
    }
  }
}
