/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

import org.junit.Assert.assertEquals
import org.junit.Test

class MiniPlayerProgressTest {
  @Test fun unavailableDurationNeverProducesInvalidProgress() {
    assertEquals(0f, miniPlayerProgress(1500L, -9223372036854775807L), 0f)
    assertEquals(0f, miniPlayerProgress(1500L, 0L), 0f)
  }
  @Test fun seekAndEndPositionsStayInsideTheTrack() {
    assertEquals(0f, miniPlayerProgress(-500L, 1000L), 0f)
    assertEquals(0.5f, miniPlayerProgress(500L, 1000L), 0f)
    assertEquals(1f, miniPlayerProgress(1500L, 1000L), 0f)
  }
}
