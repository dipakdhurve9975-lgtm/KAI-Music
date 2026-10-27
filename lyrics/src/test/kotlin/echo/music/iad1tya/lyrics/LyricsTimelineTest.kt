package echo.music.iad1tya.lyrics

import org.junit.Assert.*
import org.junit.Test

class LyricsTimelineTest {
  @Test
  fun wholeSecondsAndFractionPrecisionAreMilliseconds() {
    val rows = LyricsUtils.parseLyrics("[00:01]one\n[00:02.3]two\n[00:03.45]three\n[00:04.678]four")
    assertEquals(listOf(1000L, 2300L, 3450L, 4678L), rows.map { it.time })
  }

  @Test
  fun unsortedDuplicateTimestampsUseLastExactRow() {
    val rows = LyricsUtils.parseLyrics("[00:03]three\n[00:01]one\n[00:01]translation")
    assertEquals(-1, LyricsUtils.findCurrentLineIndex(rows, 999))
    assertEquals(1, LyricsUtils.findCurrentLineIndex(rows, 1000))
    assertEquals(1, LyricsUtils.findCurrentLineIndex(rows, 2999))
    assertEquals(2, LyricsUtils.findCurrentLineIndex(rows, 3000))
  }

  @Test
  fun blankTimedLineRepresentsInstrumentalGap() {
    val rows = LyricsUtils.parseLyrics("[00:01]one\n[00:02]\n[00:05]five")
    assertEquals("", rows[1].text)
    assertEquals(1, LyricsUtils.findCurrentLineIndex(rows, 4000))
  }

  @Test
  fun placeholderAndConstantTimesAreUntimed() {
    assertFalse(LyricsUtils.hasSyncedLyrics("[00:00.000]one\n[00:00.000]two"))
    assertFalse(LyricsUtils.hasSyncedLyrics("[00:12]one\n[00:12]two"))
    assertFalse(LyricsUtils.hasSyncedLyrics("one\ntwo"))
    assertTrue(LyricsUtils.hasSyncedLyrics("[00:00]one\n[00:02]two"))
  }

  @Test
  fun lookupHasNoHiddenLeadAndSupportsNegativeOffsetsAndSeeks() {
    val rows = LyricsUtils.parseLyrics("[00:01]one\n[00:02]two")
    assertEquals(-1, LyricsUtils.findCurrentLineIndex(rows, 999))
    assertEquals(0, LyricsUtils.findCurrentLineIndex(rows, 2000 - 100))
    assertEquals(1, LyricsUtils.findCurrentLineIndex(rows, 1900 + 100))
    assertEquals(-1, LyricsUtils.findCurrentLineIndex(emptyList(), 1000))
    assertEquals(0, LyricsUtils.findCurrentLineIndex(rows, 1000))
  }

  @Test
  fun originalHindiAndEnglishTextSurviveParsing() {
    val text = "नमस्ते दुनिया"
    assertEquals(text, LyricsUtils.parseLyrics("[00:01.100]" + text).single().text)
  }

  @Test
  fun enhancedLyricsKeepPlainTimedRows() {
    val rows =
      LyricsUtils.parseLyrics(
        "[00:01.000]<00:01.000>Hello <00:01.500>world\n[00:02.000]\n[00:04.000]Next"
      )
    assertEquals(listOf(1000L, 2000L, 4000L), rows.map { it.time })
    assertNotNull(rows.first().words)
  }
}
