/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.backdrop.Backdrop
import echo.music.iad1tya.ui.theme.KaiPalette
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import kotlin.math.abs
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** One moving lens; text/selection do not change while its drag preview crosses the labels. */
@Composable
fun KaiLiquidSegmentedControl(
  labels: List<String>,
  selectedIndex: Int,
  onSelectedIndexChange: (Int) -> Unit,
  contentColor: Color,
  modifier: Modifier = Modifier,
  backdrop: Backdrop? = null,
  glassComponent: GlassComponent = GlassComponent.PLAYER,
  minSegmentHeight: Dp = KaiSpacing.minimumTouchTarget,
  onSegmentReselected: ((Int) -> Unit)? = null,
  segmentContent: (@Composable (index: Int, selected: Boolean) -> Unit)? = null,
) {
  if (labels.isEmpty()) return
  val index = selectedIndex.coerceIn(labels.indices)
  val count = labels.size
  val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
  val reduceMotion = rememberKaiReducedMotion()
  val currentIndex by rememberUpdatedState(index)
  val currentSelectionChange by rememberUpdatedState(onSelectedIndexChange)
  val currentReselection by rememberUpdatedState(onSegmentReselected)
  val currentReducedMotion by rememberUpdatedState(reduceMotion)
  val visual = remember { LiquidSegmentVisualState(index.toFloat()) }
  val scope = rememberCoroutineScope()
  var trackWidth by remember { mutableIntStateOf(0) }
  val pressScale by
    animateFloatAsState(
      if (visual.pressed && !reduceMotion) 1.015f else 1f,
      tween(if (reduceMotion) 0 else 120),
      label = "liquidSegmentPress",
    )
  DisposableEffect(visual) {
    onDispose {
      visual.pressed = false
      visual.settleJob?.cancel()
    }
  }

  fun settle(target: Int, force: Boolean = false) {
    // Navigation confirms the release target while its spring is already running.
    // Keep that spring's velocity instead of restarting an identical animation.
    if (
      !force &&
        !visual.dragging &&
        visual.settlingTarget == target &&
        visual.settleJob?.isActive == true
    )
      return
    visual.settleJob?.cancel()
    visual.settlingTarget = target
    visual.dragging = false
    if (currentReducedMotion) {
      visual.position = target.toFloat()
      visual.stretch = 1f
      visual.stretchDirection = 0f
      return
    }
    visual.settleJob = scope.launch {
      val position = Animatable(visual.position)
      val stretch = Animatable(visual.stretch)
      coroutineScope {
        launch {
          position.animateTo(
            target.toFloat(),
            spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow),
          ) {
            visual.position = value
          }
        }
        launch {
          stretch.animateTo(1f, spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMedium)) {
            visual.stretch = value
          }
        }
      }
      visual.stretchDirection = 0f
    }
  }

  // Also cancels a preview when selection changes through accessibility, keyboard or another UI.
  LaunchedEffect(index, count, rtl, trackWidth, reduceMotion) {
    visual.pressed = false
    val context = listOf(count, rtl, trackWidth, reduceMotion)
    val changedLayout = visual.settleContext != context
    visual.settleContext = context
    settle(index, force = changedLayout)
  }

  val glassConfig = LocalGlassEffectConfig.current
  val useGlass = backdrop != null && isGlassSupported() && glassConfig.isEnabledFor(glassComponent)
  val navigationSurface = glassComponent == GlassComponent.NAV_BAR
  val lightText = contentColor.luminance() > 0.5f
  val trackColor = if (lightText) KaiPalette.DarkSurface else KaiPalette.LightSurface
  val indicatorColor = if (lightText) KaiPalette.DarkElevated else KaiPalette.LightElevated
  val selectorGlassConfig =
    glassConfig.copy(
      surfaceTintColor = indicatorColor,
      surfaceOpacity = glassConfig.surfaceOpacity.coerceIn(0.76f, 0.9f),
      lensHeight = glassConfig.lensHeight.coerceAtMost(0.12f),
      lensAmount = glassConfig.lensAmount.coerceAtMost(0.12f),
      chromaticAberration = false,
    )
  // A navigation lens sits over an already frosted bar. Tinting that existing material avoids
  // a second moving blur pass; the player selector retains its own sampled glass lens.
  val trackSurface =
    if (navigationSurface && useGlass) {
      Modifier.liquidGlass(
        config = glassConfig.forKaiOverlay(GlassComponent.NAV_BAR),
        shape = KaiShapes.pill,
        backdropOverride = backdrop,
      )
    } else {
      Modifier.background(if (useGlass) trackColor.copy(alpha = 0.8f) else trackColor)
    }

  val dragModifier =
    Modifier.onSizeChanged { trackWidth = it.width }
      .pointerInput(count, index, rtl, trackWidth) {
        try {
          awaitEachGesture {
            // Keep taps available to selectable and vertical drags available to the sheet.
            val down = awaitFirstDown(requireUnconsumed = false)
            visual.pressed = true
            visual.stretchDirection = 0f
            val pointer = down.id
            val initialPosition = visual.position
            fun positionAt(pointerX: Float) =
              liquidSegmentDragPosition(
                pointerX = pointerX,
                downX = down.position.x,
                initialPosition = initialPosition,
                selectedIndex = currentIndex,
                trackWidth = size.width.toFloat(),
                segmentCount = count,
                rightToLeft = rtl,
              )
            var claimed = false
            var released = false
            var verticalGesture = false
            while (!released) {
              // Inspect release before child click handling: a very fast swipe may have no MOVE.
              val event = awaitPointerEvent(PointerEventPass.Initial)
              val change = event.changes.firstOrNull { it.id == pointer }
              if (event.changes.count { it.pressed } > 1) {
                // Cancel the child's tap too; two fingers must not select a destination on UP.
                event.changes.forEach { it.consume() }
                break
              }
              if (change == null || change.isConsumed) break
              val delta = change.position - down.position
              val axis = liquidSegmentGestureAxis(delta.x, delta.y, viewConfiguration.touchSlop)
              if (!change.pressed) {
                if (verticalGesture || (!claimed && axis == LiquidSegmentGestureAxis.VERTICAL)) {
                  // An unclaimed vertical move inside a cell must cancel its click on UP.
                  change.consume()
                } else if (claimed || axis == LiquidSegmentGestureAxis.HORIZONTAL) {
                  change.consume()
                  // UP can carry a newer position than the final MOVE. Commit exactly once.
                  visual.position = positionAt(change.position.x)
                  val destination =
                    liquidSegmentDestination(
                      visual.position,
                      currentIndex,
                      count,
                      cancelled = false,
                    )
                  settle(destination)
                  if (destination != currentIndex) currentSelectionChange(destination)
                }
                released = true
              } else {
                if (verticalGesture) {
                  // A scrolling parent keeps its MOVE and UP/fling; its consumption also cancels
                  // the child's click. Otherwise retain the latch until we suppress that click.
                  val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                  if (finalEvent.changes.firstOrNull { it.id == pointer }?.isConsumed != false)
                    break
                  continue
                }
                if (!claimed) {
                  when (axis) {
                    LiquidSegmentGestureAxis.VERTICAL -> {
                      verticalGesture = true
                      visual.pressed = false
                      val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                      if (finalEvent.changes.firstOrNull { it.id == pointer }?.isConsumed != false)
                        break
                      continue
                    }
                    LiquidSegmentGestureAxis.HORIZONTAL -> {
                      claimed = true
                      visual.settleJob?.cancel()
                      visual.settlingTarget = null
                      visual.dragging = true
                    }
                    LiquidSegmentGestureAxis.PENDING -> {
                      // Another handler can consume after Initial; respect that cancellation.
                      val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                      if (finalEvent.changes.firstOrNull { it.id == pointer }?.isConsumed != false)
                        break
                      continue
                    }
                  }
                }
                if (claimed) {
                  val segmentWidth = size.width.toFloat() / count
                  val physicalDelta = change.position.x - change.previousPosition.x
                  if (physicalDelta != 0f)
                    visual.stretchDirection = if (physicalDelta > 0f) 1f else -1f
                  visual.position = positionAt(change.position.x)
                  visual.stretch =
                    if (currentReducedMotion || segmentWidth <= 0f) 1f
                    else 1f + (abs(physicalDelta) / segmentWidth * 0.12f).coerceAtMost(0.06f)
                  change.consume()
                }
              }
            }
            visual.pressed = false
            if (claimed && !released) {
              settle(
                liquidSegmentDestination(visual.position, currentIndex, count, cancelled = true)
              )
            }
          }
        } finally {
          visual.pressed = false
          if (visual.dragging) settle(currentIndex)
        }
      }

  Box(
    modifier
      .fillMaxWidth()
      .selectableGroup()
      .clip(KaiShapes.pill)
      .then(trackSurface)
      .border(1.dp, contentColor.copy(alpha = 0.10f), KaiShapes.pill)
      .padding(KaiSpacing.xxs)
      .then(dragModifier)
  ) {
    Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
      Box(
        Modifier.fillMaxHeight()
          .fillMaxWidth(1f / count)
          .graphicsLayer {
            // Snapshot reads belong to the layer: dragging does not recompose the labels.
            translationX =
              (liquidSegmentPhysicalPosition(visual.position, count, rtl) - (count - 1) / 2f) *
                size.width
            transformOrigin =
              TransformOrigin(
                if (visual.stretchDirection > 0f) 0f
                else if (visual.stretchDirection < 0f) 1f else 0.5f,
                0.5f,
              )
            scaleX = visual.stretch * pressScale
            scaleY =
              (if (currentReducedMotion) 1f else 1f + (visual.stretch - 1f) * 0.2f) * pressScale
          }
          .padding(1.dp)
          .clip(KaiShapes.pill)
          .then(
            if (navigationSurface)
              Modifier.background(contentColor.copy(alpha = if (lightText) 0.15f else 0.08f))
            else if (useGlass)
              Modifier.liquidGlass(
                config = selectorGlassConfig,
                shape = KaiShapes.pill,
                backdropOverride = backdrop,
              )
            else Modifier.background(indicatorColor)
          )
          .border(1.dp, contentColor.copy(alpha = 0.16f), KaiShapes.pill)
          .drawWithCache {
            val rim =
              Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                Offset.Zero,
                Offset(0f, size.height),
              )
            onDrawBehind { drawRect(rim) }
          }
      )
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      labels.forEachIndexed { tabIndex, label ->
        val chooseLabel = stringResource(R.string.kai_choose_segment, label)
        val chooseTab: () -> Unit = {
          settle(tabIndex)
          if (tabIndex != currentIndex) currentSelectionChange(tabIndex)
          else currentReselection?.invoke(tabIndex)
        }
        Box(
          Modifier.weight(1f)
            .heightIn(min = minSegmentHeight)
            .clip(KaiShapes.pill)
            .selectable(selected = index == tabIndex, role = Role.Tab, onClick = chooseTab)
            .semantics {
              if (segmentContent != null) contentDescription = label
              onClick(label = chooseLabel) {
                chooseTab()
                true
              }
            }
            .padding(
              horizontal = KaiSpacing.xs,
              vertical = if (navigationSurface) KaiSpacing.xs else KaiSpacing.sm,
            ),
          contentAlignment = Alignment.Center,
        ) {
          if (segmentContent != null) {
            segmentContent(tabIndex, index == tabIndex)
          } else
            Text(
              label,
              color = contentColor,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = if (index == tabIndex) FontWeight.SemiBold else FontWeight.Medium,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Center,
            )
        }
      }
    }
  }
}

@Stable
private class LiquidSegmentVisualState(initial: Float) {
  var position by mutableFloatStateOf(initial)
  var stretch by mutableFloatStateOf(1f)
  var stretchDirection by mutableFloatStateOf(0f)
  var pressed by mutableStateOf(false)
  var dragging = false
  var settlingTarget: Int? = null
  var settleContext: List<Any>? = null
  var settleJob: Job? = null
}
