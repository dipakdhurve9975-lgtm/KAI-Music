/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KaiGlassPolicyTest {
  @Test
  fun unsupportedAndroidVersionsUseFallbackInsteadOfRenderEffect() {
    assertFalse(isGlassSupported(26))
    assertFalse(isGlassSupported(30))
    assertTrue(isGlassSupported(31))
    assertTrue(isGlassSupported(36))
  }

  @Test
  fun savedMasterAndComponentOptOutsRemainEffective() {
    val disabled = GlassEffectConfig(globalEnabled = false)
    GlassComponent.entries.forEach { assertFalse(disabled.isEnabledFor(it)) }
    val selected =
      GlassEffectConfig(globalEnabled = true, navBarEnabled = false, miniPlayerEnabled = true)
    assertFalse(selected.isEnabledFor(GlassComponent.NAV_BAR))
    assertTrue(selected.isEnabledFor(GlassComponent.MINI_PLAYER))
  }
}
