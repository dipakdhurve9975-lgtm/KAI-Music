/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import echo.music.iad1tya.constants.LyricsPresentationStyle
import echo.music.iad1tya.lyrics.WordTimestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LyricsPresentationPolicyTest {
  @Test
  fun `source spans preserve exact punctuation whitespace and repeated words`() {
    val text = "Hello,  hello — world!"
    val spans =
      sourceLyricSpans(
        text,
        listOf(
          WordTimestamp("Hello", 1.0, 1.3),
          WordTimestamp("hello", 1.4, 1.8),
          WordTimestamp("world", 1.9, 2.4),
        ),
      )
    assertEquals(listOf("Hello", "hello", "world"), spans.map { text.substring(it.start, it.end) })
    assertEquals(listOf(1000L, 1400L, 1900L), spans.map { it.startTimeMs })
    assertEquals("Hello,  hello — world!", text)
  }

  @Test
  fun `line-only lyrics never acquire invented word timestamps`() {
    assertTrue(sourceLyricSpans("A whole line", null).isEmpty())
    assertTrue(sourceLyricSpans("A whole line", emptyList()).isEmpty())
  }

  @Test
  fun `mismatched transliteration cannot reuse original word timing`() {
    assertTrue(sourceLyricSpans("Tum mere ho", listOf(WordTimestamp("तुम", 1.0, 2.0))).isEmpty())
  }

  @Test
  fun `Hindi whole words retain original shaped strings`() {
    val text = "तुम मेरे हो"
    val spans =
      sourceLyricSpans(
        text,
        listOf(
          WordTimestamp("तुम", 1.0, 1.5),
          WordTimestamp("मेरे", 1.5, 2.0),
          WordTimestamp("हो", 2.0, 2.5),
        ),
      )
    assertEquals(listOf("तुम", "मेरे", "हो"), spans.map { text.substring(it.start, it.end) })
  }

  @Test
  fun `partial matras conjuncts combining marks and surrogate pairs fall back to line`() {
    listOf(
        "गाना" to listOf("ग", "ाना"),
        "क्षमा" to listOf("क्", "षमा"),
        "e\u0301" to listOf("e", "\u0301"),
        "\uD83D\uDE00" to listOf("\uD83D", "\uDE00"),
      )
      .forEach { (text, fragments) ->
        assertTrue(
          text,
          sourceLyricSpans(
              text,
              fragments.mapIndexed { i, token ->
                WordTimestamp(token, i.toDouble(), i + 1.0)
              },
            )
            .isEmpty(),
        )
      }
  }

  @Test
  fun `emoji joiners modifiers and regional flags cannot be split`() {
    listOf(
        "👩‍🎤" to listOf("👩", "‍🎤"),
        "👍🏽" to listOf("👍", "🏽"),
        "🇮🇳" to listOf("🇮", "🇳"),
      )
      .forEach { (text, fragments) ->
        assertTrue(
          text,
          sourceLyricSpans(
              text,
              fragments.mapIndexed { i, token ->
                WordTimestamp(token, i.toDouble(), i + 1.0)
              },
            )
            .isEmpty(),
        )
      }
  }

  @Test
  fun `invalid and descending timestamps use whole-line fallback`() {
    listOf(
        listOf(WordTimestamp("one", Double.NaN, 2.0)),
        listOf(WordTimestamp("one", 1.0, Double.POSITIVE_INFINITY)),
        listOf(WordTimestamp("one", -1.0, 2.0)),
        listOf(WordTimestamp("one", 2.0, 1.0)),
        listOf(WordTimestamp("one", 2.0, 3.0), WordTimestamp("two", 1.0, 2.0)),
      )
      .forEach { assertTrue(sourceLyricSpans("one two", it).isEmpty()) }
  }

  @Test
  fun `incomplete word coverage does not hide missing original words`() {
    assertTrue(sourceLyricSpans("one two three", listOf(WordTimestamp("two", 1.0, 2.0))).isEmpty())
  }

  @Test
  fun `persisted Auto pin survives reentering current song`() {
    assertEquals(
      LyricsPresentationStyle.CINEMATIC,
      resolveLyricsPresentation(
        LyricsPresentationStyle.CLASSIC_FLOW,
        true,
        "track-a",
        "track-a",
        LyricsPresentationStyle.CINEMATIC,
      ),
    )
  }

  @Test
  fun `manual mode remains stable across songs`() {
    listOf("track-a", "track-b", null).forEach { id ->
      assertEquals(
        LyricsPresentationStyle.MINIMAL_FOCUS,
        resolveLyricsPresentation(
          LyricsPresentationStyle.MINIMAL_FOCUS,
          false,
          id,
          "other",
          LyricsPresentationStyle.FLOATING,
        ),
      )
    }
  }

  @Test
  fun `Auto changes at song boundary and never selects timing-dependent Kinetic`() {
    LyricsPresentationStyle.entries.forEach { previous ->
      repeat(50) { song ->
        val next = nextAutoLyricsPresentation("song-$song", previous)
        assertFalse(next == LyricsPresentationStyle.KINETIC)
        assertFalse(next == previous)
        assertEquals(next, nextAutoLyricsPresentation("song-$song", previous))
      }
    }
  }

  @Test
  fun `unknown saved style falls back without changing existing animation prefs`() {
    assertEquals(LyricsPresentationStyle.CLASSIC_FLOW, parseLyricsPresentation("obsolete-style"))
    assertEquals(LyricsPresentationStyle.CLASSIC_FLOW, parseLyricsPresentation(null))
  }

  @Test
  fun `entrance follows audio seek and remains frozen at a paused position`() {
    assertEquals(0f, lyricEntranceProgress(1000, 2000, false))
    assertEquals(.5f, lyricEntranceProgress(2240, 2000, false))
    assertEquals(1f, lyricEntranceProgress(4000, 2000, false))
    assertEquals(lyricEntranceProgress(2240, 2000, false), lyricEntranceProgress(2240, 2000, false))
    assertEquals(1f, lyricEntranceProgress(2000, 2000, true))
  }
}
