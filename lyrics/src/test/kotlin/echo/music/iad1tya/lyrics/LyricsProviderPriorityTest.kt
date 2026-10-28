/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.lyrics

import org.junit.Assert.*
import org.junit.Test

class LyricsProviderPriorityTest {
  @Test
  fun racingLowerPriorityResponseWaitsForRequestedProvider() {
    assertNull(readyTimedLyricsProvider(booleanArrayOf(false, true), setOf(1), false))
    assertEquals(0, readyTimedLyricsProvider(booleanArrayOf(true, true), setOf(0, 1), false))
  }

  @Test
  fun failedHigherPriorityProviderDoesNotBlockValidFallback() {
    assertEquals(1, readyTimedLyricsProvider(booleanArrayOf(true, true), setOf(1), false))
  }

  @Test
  fun explicitFasterPreferenceKeepsFirstTimedResponse() {
    assertEquals(1, readyTimedLyricsProvider(booleanArrayOf(false, true), setOf(1), true))
  }
}
