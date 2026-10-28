/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

import org.junit.Assert.*
import org.junit.Test

class PlayerVisibilityPolicyTest {
  @Test
  fun aNearlyExpandedPlayerKeepsItsTimelineActive() {
    assertTrue(isPlayerPresentationVisible(.99999f))
    assertTrue(isPlayerPresentationVisible(1f))
    assertTrue(isPlayerPresentationVisible(.8f))
  }

  @Test
  fun collapsedAndDismissedSurfacesStopPresentationWork() {
    assertFalse(isPlayerPresentationVisible(0f))
    assertFalse(isPlayerPresentationVisible(-.1f))
    assertFalse(isPlayerPresentationVisible(.2f))
    assertFalse(isPlayerPresentationVisible(Float.NaN))
  }
}
