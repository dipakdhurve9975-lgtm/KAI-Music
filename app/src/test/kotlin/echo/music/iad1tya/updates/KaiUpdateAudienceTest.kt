// Modified for KAI Music on 2026-10-29; GPL-3.0.
package echo.music.iad1tya.updates

import org.junit.Assert.*
import org.junit.Test

class KaiUpdateAudienceTest {
  @Test
  fun publishedPreviewsReceivePreviewUpdatesWithoutASettingsVisit() {
    assertTrue(includePreviewUpdates(previewBuild = true, saved = null))
    assertFalse(includePreviewUpdates(previewBuild = false, saved = null))
    assertFalse(includePreviewUpdates(previewBuild = true, saved = false))
    assertTrue(includePreviewUpdates(previewBuild = false, saved = true))
  }

  @Test
  fun oldStableCheckDoesNotSuppressTheFirstPreviewCheckAfterUpgrade() {
    val now = 10_000_000L
    assertTrue(shouldCheckUpdateForChannel(false, true, null, now, now - 1000, now - 1000))
    assertTrue(shouldCheckUpdateForChannel(false, true, false, now, now - 1000, now - 1000))
    assertFalse(shouldCheckUpdateForChannel(false, true, true, now, now - 1000, now - 1000))
    assertTrue(shouldCheckUpdateForChannel(true, true, true, now, now - 1000, now - 1000))
  }

  @Test
  fun successfulChecksRefreshAfterAnHourAndFailuresStillBackOff() {
    val now = 10_000_000L
    assertFalse(shouldCheckUpdate(now, now - 3_599_000, now - 3_599_000))
    assertTrue(shouldCheckUpdate(now, now - 3_600_001, now - 3_600_001))
    assertFalse(shouldCheckUpdate(now, now - 1000, 0))
  }
}
