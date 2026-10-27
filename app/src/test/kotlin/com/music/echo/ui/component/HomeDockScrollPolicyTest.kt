package echo.music.iad1tya.ui.component

import org.junit.Assert.*
import org.junit.Test

class HomeDockScrollPolicyTest {
  @Test
  fun smallMotionDoesNotHideDock() {
    val policy = HomeDockScrollPolicy(12f)
    assertFalse(policy.onScroll(-5f))
    assertFalse(policy.onScroll(-6f))
    assertTrue(policy.onScroll(-2f))
  }

  @Test
  fun reversingDirectionRequiresFreshThreshold() {
    val policy = HomeDockScrollPolicy(12f)
    assertTrue(policy.onScroll(-40f))
    assertTrue(policy.onScroll(6f))
    assertFalse(policy.onScroll(7f))
  }

  @Test
  fun idleAndInvalidMotionDoNotChangeSelection() {
    val policy = HomeDockScrollPolicy(12f)
    policy.onScroll(-20f)
    assertTrue(policy.onScroll(0f))
    assertTrue(policy.onScroll(Float.NaN))
    policy.reveal()
    assertFalse(policy.hidden)
    assertFalse(policy.onScroll(-1f))
  }
}
