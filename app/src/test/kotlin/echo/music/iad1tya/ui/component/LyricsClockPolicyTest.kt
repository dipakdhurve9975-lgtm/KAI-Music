/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import org.junit.Assert.*
import org.junit.Test

class LyricsClockPolicyTest {
  @Test
  fun aPlayingReadySessionAdvancesWithoutAnExtraPlayerListener() {
    assertTrue(lyricsTimelineShouldAdvance(true, true, false, false, false))
  }

  @Test
  fun pausedAndBufferingLocalSessionsStopTimelineWork() {
    assertFalse(lyricsTimelineShouldAdvance(false, true, false, true, false))
    assertFalse(lyricsTimelineShouldAdvance(true, false, false, true, false))
  }

  @Test
  fun castingUsesTheReceiverState() {
    assertTrue(lyricsTimelineShouldAdvance(false, false, true, true, false))
    assertFalse(lyricsTimelineShouldAdvance(true, true, true, false, false))
    assertFalse(lyricsTimelineShouldAdvance(true, true, true, true, true))
  }
}
