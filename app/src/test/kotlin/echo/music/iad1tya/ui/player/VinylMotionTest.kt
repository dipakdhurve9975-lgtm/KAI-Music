package echo.music.iad1tya.ui.player

import org.junit.Assert.assertEquals
import org.junit.Test

class VinylMotionTest {
  @Test
  fun recordMakesOneRevolutionInOnePointEightSeconds() {
    assertEquals(37f, advanceVinylAngle(37f, 1_800_000_000L), 0.001f)
    assertEquals(237f, advanceVinylAngle(37f, 1_000_000_000L), 0.001f)
  }

  @Test
  fun pauseAndResumePreserveTheAngleAcrossTheWrap() {
    val beforePause = advanceVinylAngle(350f, 25_000_000L)
    assertEquals(355f, beforePause, 0.001f)
    assertEquals(beforePause, advanceVinylAngle(beforePause, 0L), 0f)
    assertEquals(5f, advanceVinylAngle(beforePause, 50_000_000L), 0.001f)
  }

  @Test
  fun frameRateDoesNotChangeSpeed() {
    var angle = 0f
    repeat(100) { angle = advanceVinylAngle(angle, 10_000_000L) }
    assertEquals(advanceVinylAngle(0f, 1_000_000_000L), angle, 0.001f)
  }
}
