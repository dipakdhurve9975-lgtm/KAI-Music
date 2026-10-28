/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import org.junit.Assert.assertEquals
import org.junit.Test

class LiquidSegmentMotionTest {
  @Test
  fun pointerAtEachCellCenterSelectsThatCell() {
    for (width in listOf(240f, 320f, 613f)) {
      for (count in listOf(3, 5)) {
        for (index in 0 until count) {
          val center = (index + 0.5f) * width / count
          assertEquals(index.toFloat(), liquidSegmentPosition(center, width, count, false), 0.0001f)
        }
      }
    }
  }

  @Test
  fun rtlMirrorsPhysicalCellsWithoutChangingLogicalModeOrder() {
    assertEquals(2f, liquidSegmentPosition(50f, 300f, 3, true), 0f)
    assertEquals(1f, liquidSegmentPosition(150f, 300f, 3, true), 0f)
    assertEquals(0f, liquidSegmentPosition(250f, 300f, 3, true), 0f)
  }

  @Test
  fun indicatorAndPointerMappingAgreeInBothDirections() {
    for (rtl in listOf(false, true)) {
      for (logical in listOf(0f, 0.25f, 0.9f, 1.5f, 2f)) {
        val physical = liquidSegmentPhysicalPosition(logical, 3, rtl)
        val pointer = (physical + 0.5f) * 100f
        assertEquals(logical, liquidSegmentPosition(pointer, 300f, 3, rtl), 0.0001f)
      }
    }
  }

  @Test
  fun draggingBeyondTrackClampsToEndModes() {
    assertEquals(0f, liquidSegmentPosition(-500f, 300f, 3, false), 0f)
    assertEquals(2f, liquidSegmentPosition(900f, 300f, 3, false), 0f)
    assertEquals(2f, liquidSegmentPosition(-500f, 300f, 3, true), 0f)
    assertEquals(0f, liquidSegmentPosition(900f, 300f, 3, true), 0f)
  }

  @Test
  fun layoutBeforeMeasurementAndInvalidCoordinatesCannotProduceNaN() {
    assertEquals(0f, liquidSegmentPosition(20f, 0f, 3, false), 0f)
    assertEquals(0f, liquidSegmentPosition(Float.NaN, 300f, 3, false), 0f)
    assertEquals(0f, liquidSegmentPosition(20f, Float.POSITIVE_INFINITY, 3, true), 0f)
    assertEquals(0f, liquidSegmentPhysicalPosition(Float.NaN, 1, false), 0f)
    assertEquals(0, liquidSegmentReleaseIndex(Float.NaN, 3))
    assertEquals(0, liquidSegmentReleaseIndex(2f, 0))
  }

  @Test
  fun releaseChoosesNearestModeRatherThanLastCrossedMode() {
    assertEquals(0, liquidSegmentReleaseIndex(0.49f, 3))
    assertEquals(1, liquidSegmentReleaseIndex(0.51f, 3))
    assertEquals(1, liquidSegmentReleaseIndex(1.49f, 3))
    assertEquals(2, liquidSegmentReleaseIndex(1.51f, 3))
    assertEquals(2, liquidSegmentReleaseIndex(30f, 3))
  }

  @Test
  fun cancellationRestoresExternalSelectionAfterPreviewCrossesEveryMode() {
    for (selected in 0..2) {
      for (preview in listOf(0f, 0.8f, 1.7f, 2f)) {
        assertEquals(selected, liquidSegmentDestination(preview, selected, 3, cancelled = true))
      }
    }
    assertEquals(2, liquidSegmentDestination(1.9f, 0, 3, cancelled = false))
  }

  @Test
  fun movementInsideTouchSlopDoesNotClaimTap() {
    assertEquals(LiquidSegmentGestureAxis.PENDING, liquidSegmentGestureAxis(7f, 3f, 8f))
    assertEquals(LiquidSegmentGestureAxis.PENDING, liquidSegmentGestureAxis(-8f, 0f, 8f))
  }

  @Test
  fun verticalAndDiagonalScrollsRemainAvailableToParent() {
    assertEquals(LiquidSegmentGestureAxis.VERTICAL, liquidSegmentGestureAxis(3f, 12f, 8f))
    assertEquals(LiquidSegmentGestureAxis.VERTICAL, liquidSegmentGestureAxis(-12f, -12f, 8f))
    assertEquals(LiquidSegmentGestureAxis.VERTICAL, liquidSegmentGestureAxis(Float.NaN, 0f, 8f))
  }

  @Test
  fun horizontalDragClaimsOnlyAfterDominantHorizontalSlop() {
    assertEquals(LiquidSegmentGestureAxis.HORIZONTAL, liquidSegmentGestureAxis(12f, 3f, 8f))
    assertEquals(LiquidSegmentGestureAxis.HORIZONTAL, liquidSegmentGestureAxis(-13f, -8f, 8f))
  }

  @Test
  fun selectedLensKeepsItsGrabOffsetAtEitherEdge() {
    for (down in listOf(105f, 150f, 195f)) {
      assertEquals(1f, liquidSegmentDragPosition(down, down, 1f, 1, 300f, 3, false), 0f)
      assertEquals(
        1.12f,
        liquidSegmentDragPosition(down + 12f, down, 1f, 1, 300f, 3, false),
        0.0001f,
      )
      assertEquals(
        0.85f,
        liquidSegmentDragPosition(down - 15f, down, 1f, 1, 300f, 3, false),
        0.0001f,
      )
    }
  }

  @Test
  fun rtlGrabOffsetTracksLogicalMovementInTheOppositeDirection() {
    assertEquals(0.75f, liquidSegmentDragPosition(215f, 190f, 1f, 1, 300f, 3, true), 0f)
    assertEquals(1.25f, liquidSegmentDragPosition(165f, 190f, 1f, 1, 300f, 3, true), 0f)
    assertEquals(1f, liquidSegmentDragPosition(190f, 190f, 1f, 1, 300f, 3, true), 0f)
  }

  @Test
  fun grabbingDuringAProgrammaticTransitionRetainsTheVisiblePosition() {
    assertEquals(0.85f, liquidSegmentDragPosition(160f, 150f, 0.75f, 1, 300f, 3, false), 0.0001f)
  }

  @Test
  fun dragFromAnUnselectedCellFollowsItsPhysicalLocation() {
    assertEquals(1.2f, liquidSegmentDragPosition(170f, 150f, 0f, 0, 300f, 3, false), 0.0001f)
    assertEquals(0.8f, liquidSegmentDragPosition(170f, 150f, 0f, 0, 300f, 3, true), 0.0001f)
  }

  @Test
  fun finalReleaseCoordinateWinsOverTheLastMove() {
    val lastMove = liquidSegmentDragPosition(190f, 50f, 0f, 0, 300f, 3, false)
    val release = liquidSegmentDragPosition(230f, 50f, 0f, 0, 300f, 3, false)
    assertEquals(1, liquidSegmentDestination(lastMove, 0, 3, cancelled = false))
    assertEquals(2, liquidSegmentDestination(release, 0, 3, cancelled = false))
    assertEquals(0, liquidSegmentDestination(release, 0, 3, cancelled = true))
  }

  @Test
  fun releaseOnlySwipeHasADestinationWithoutIntermediateMoves() {
    assertEquals(LiquidSegmentGestureAxis.HORIZONTAL, liquidSegmentGestureAxis(180f, 2f, 8f))
    val release = liquidSegmentDragPosition(230f, 50f, 0f, 0, 300f, 3, false)
    assertEquals(2, liquidSegmentDestination(release, 0, 3, cancelled = false))
    val rtlRelease = liquidSegmentDragPosition(70f, 250f, 0f, 0, 300f, 3, true)
    assertEquals(2, liquidSegmentDestination(rtlRelease, 0, 3, cancelled = false))
    assertEquals(LiquidSegmentGestureAxis.VERTICAL, liquidSegmentGestureAxis(2f, 180f, 8f))
  }

  @Test
  fun grabOffsetClampsBeyondTheTrackAndSurvivesInvalidMeasurements() {
    assertEquals(2f, liquidSegmentDragPosition(900f, 150f, 1f, 1, 300f, 3, false), 0f)
    assertEquals(0f, liquidSegmentDragPosition(-900f, 150f, 1f, 1, 300f, 3, false), 0f)
    assertEquals(1f, liquidSegmentDragPosition(250f, 150f, 1f, 1, 0f, 3, false), 0f)
    assertEquals(1f, liquidSegmentDragPosition(Float.NaN, 150f, 1f, 1, 300f, 3, false), 0f)
  }
}
