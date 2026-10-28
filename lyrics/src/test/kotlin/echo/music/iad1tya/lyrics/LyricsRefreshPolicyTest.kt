/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.lyrics

import echo.music.iad1tya.db.entities.LyricsEntity
import org.junit.Assert.*
import org.junit.Test

class LyricsRefreshPolicyTest {
  @Test
  fun failedRefreshKeepsReadableLyrics() {
    assertFalse(canReplaceLyricsAfterRefresh("saved", "saved", null))
    assertFalse(canReplaceLyricsAfterRefresh("saved", "saved", ""))
    assertFalse(canReplaceLyricsAfterRefresh("saved", "saved", LyricsEntity.LYRICS_NOT_FOUND))
  }

  @Test
  fun aConcurrentUserEditWinsOverTheNetworkResponse() {
    assertFalse(canReplaceLyricsAfterRefresh("saved", "edited", "fetched"))
  }

  @Test
  fun aSuccessfulRefreshReplacesOnlyTheUnchangedRecord() {
    assertTrue(canReplaceLyricsAfterRefresh("saved", "saved", "fetched"))
    assertTrue(canReplaceLyricsAfterRefresh(null, null, "fetched"))
  }
}
