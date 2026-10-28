/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package com.music.youlyplus

import com.music.youlyplus.YouLyPlus.convertToLrc
import com.music.youlyplus.models.LyricsItem
import com.music.youlyplus.models.LyricsMetadata
import com.music.youlyplus.models.LyricsResponse
import com.music.youlyplus.models.Syllable
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.*
import org.junit.Test

class LyricsTimingTest {
  @Test
  fun missingOrConstantTimesRemainReadablePlainText() {
    assertEquals("one\ntwo", listOf(LyricsItem("one"), LyricsItem("two")).convertToLrc())
    assertEquals("one\ntwo", listOf(LyricsItem("one", 0), LyricsItem("two", 0)).convertToLrc())
  }

  @Test
  fun actualLineTimesKeepMillisecondPrecision() {
    val text = listOf(LyricsItem("one", 1000), LyricsItem("two", 2345)).convertToLrc()
    assertEquals("[00:01.000]one\n[00:02.345]two", text)
  }

  @Test
  fun syllableTimingNeverAddsSpacesInsideSourceWords() {
    val line =
      LyricsItem(
        "body नमस्ते",
        1000,
        syllabus =
          listOf(
            Syllable("bo", 1000),
            Syllable("dy", 1200),
            Syllable("नम", 1400),
            Syllable("स्ते", 1600),
          ),
      )
    assertEquals(
      "[00:01.000]<00:01.000>bo<00:01.200>dy <00:01.400>नम<00:01.600>स्ते",
      listOf(line).convertToLrc(),
    )
  }

  @Test
  fun incompatibleFragmentsFallBackToOriginalLineTiming() {
    val line = LyricsItem("Original text", 1000, syllabus = listOf(Syllable("Different", 1000)))
    assertEquals("[00:01.000]Original text", listOf(line).convertToLrc())
  }

  @Test
  fun explicitEditAndDurationMismatchesAreRejected() {
    assertFalse(
      YouLyPlus.matchesRequestedVersion(
        LyricsResponse(trackName = "Song", duration = 180.0),
        "Song (Slowed)",
        220,
      )
    )
    assertFalse(
      YouLyPlus.matchesRequestedVersion(
        LyricsResponse(trackName = "Song", duration = 220.0),
        "Song (Slowed)",
        220,
      )
    )
    assertTrue(
      YouLyPlus.matchesRequestedVersion(
        LyricsResponse(trackName = "Song (Slowed)", duration = 220.0),
        "Song (Slowed)",
        220,
      )
    )
    assertTrue(YouLyPlus.matchesRequestedVersion(LyricsResponse(), "Song", 220))
  }

  @Test
  fun timedMirrorSelectionRejectsPlaceholderProgression() {
    assertFalse(YouLyPlus.hasUsableSync("[00:00]a\n[00:00]b"))
    assertFalse(YouLyPlus.hasUsableSync("plain text"))
    assertTrue(YouLyPlus.hasUsableSync("[00:01.5]a\n[00:02]b"))
  }

  @Test
  fun kpoeNestedClockDurationGuardsDifferentRecordings() {
    val response = LyricsResponse(metadata = LyricsMetadata(JsonPrimitive("3:53.713")))
    assertTrue(YouLyPlus.matchesRequestedVersion(response, "Shape of You", 234))
    assertFalse(YouLyPlus.matchesRequestedVersion(response, "Shape of You (Slowed)", 280))
  }
}
