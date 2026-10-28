/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import kotlin.math.abs
import kotlin.math.roundToInt

/** Physical pointer coordinates map to logical indices; mode order stays stable in RTL. */
internal fun liquidSegmentPosition(
  pointerX: Float,
  trackWidth: Float,
  segmentCount: Int,
  rightToLeft: Boolean,
): Float {
  if (segmentCount <= 1 || !trackWidth.isFinite() || trackWidth <= 0f || !pointerX.isFinite()) {
    return 0f
  }
  val physical = (pointerX / (trackWidth / segmentCount) - 0.5f).coerceIn(0f, segmentCount - 1f)
  return if (rightToLeft) segmentCount - 1f - physical else physical
}

internal fun liquidSegmentPhysicalPosition(
  position: Float,
  segmentCount: Int,
  rightToLeft: Boolean,
): Float {
  val lastIndex = (segmentCount - 1).coerceAtLeast(0).toFloat()
  val safe = if (position.isFinite()) position.coerceIn(0f, lastIndex) else 0f
  return if (rightToLeft) lastIndex - safe else safe
}

/** A lens grabbed away from its center keeps that offset instead of jumping to the finger. */
internal fun liquidSegmentDragPosition(
  pointerX: Float,
  downX: Float,
  initialPosition: Float,
  selectedIndex: Int,
  trackWidth: Float,
  segmentCount: Int,
  rightToLeft: Boolean,
): Float {
  val lastIndex = (segmentCount - 1).coerceAtLeast(0).toFloat()
  val initial = if (initialPosition.isFinite()) initialPosition.coerceIn(0f, lastIndex) else 0f
  if (
    segmentCount <= 1 ||
      !trackWidth.isFinite() ||
      trackWidth <= 0f ||
      !pointerX.isFinite() ||
      !downX.isFinite()
  )
    return initial

  val touchedIndex =
    liquidSegmentReleaseIndex(
      liquidSegmentPosition(downX, trackWidth, segmentCount, rightToLeft),
      segmentCount,
    )
  if (touchedIndex != selectedIndex) {
    // A drag beginning on another tab brings the lens to that tab, just like its tap action.
    return liquidSegmentPosition(pointerX, trackWidth, segmentCount, rightToLeft)
  }
  val direction = if (rightToLeft) -1f else 1f
  val displacement = (pointerX - downX) / (trackWidth / segmentCount) * direction
  return (initial + displacement).coerceIn(0f, lastIndex)
}

internal fun liquidSegmentReleaseIndex(position: Float, segmentCount: Int): Int =
  if (segmentCount <= 1 || !position.isFinite()) 0
  else position.roundToInt().coerceIn(0, segmentCount - 1)

/**
 * Once a vertical gesture wins, the selector leaves the pointer unconsumed for its scroll parent.
 */
internal enum class LiquidSegmentGestureAxis {
  PENDING,
  HORIZONTAL,
  VERTICAL,
}

internal fun liquidSegmentGestureAxis(
  deltaX: Float,
  deltaY: Float,
  touchSlop: Float,
): LiquidSegmentGestureAxis {
  val slop = touchSlop.coerceAtLeast(0f)
  if (!deltaX.isFinite() || !deltaY.isFinite()) return LiquidSegmentGestureAxis.VERTICAL
  return when {
    abs(deltaY) > slop && abs(deltaY) >= abs(deltaX) -> LiquidSegmentGestureAxis.VERTICAL
    abs(deltaX) > slop && abs(deltaX) > abs(deltaY) -> LiquidSegmentGestureAxis.HORIZONTAL
    else -> LiquidSegmentGestureAxis.PENDING
  }
}

/** Only release has a destination. Cancellation keeps the externally selected mode. */
internal fun liquidSegmentDestination(
  previewPosition: Float,
  selectedIndex: Int,
  segmentCount: Int,
  cancelled: Boolean,
): Int =
  if (cancelled) selectedIndex.coerceIn(0, (segmentCount - 1).coerceAtLeast(0))
  else liquidSegmentReleaseIndex(previewPosition, segmentCount)
