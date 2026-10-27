package echo.music.iad1tya.ui.component

import org.junit.Assert.*
import org.junit.Test

class KaiRootTransitionTest {
  @Test
  fun allPrimaryRootSwitchesAreImmediate() {
    val roots = listOf("home", "search_input", "recognition", "library")
    for (a in roots) for (b in roots) assertTrue(kaiImmediateRootTransition(a, b))
  }

  @Test
  fun detailTransitionsKeepTheirSeparatePolicy() {
    assertFalse(kaiImmediateRootTransition("home", "album/id"))
    assertFalse(kaiImmediateRootTransition(null, "home"))
  }
}
