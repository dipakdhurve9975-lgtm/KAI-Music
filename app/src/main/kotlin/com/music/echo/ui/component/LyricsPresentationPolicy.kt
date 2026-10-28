/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import echo.music.iad1tya.constants.LyricsPresentationStyle
import echo.music.iad1tya.lyrics.WordTimestamp
import java.text.BreakIterator
import java.util.Locale

internal data class TimedLyricSpan(val start: Int, val end: Int, val startTimeMs: Long)

/** Match source tokens back onto the original string; never reconstruct text or invent timing. */
internal fun sourceLyricSpans(text: String, words: List<WordTimestamp>?): List<TimedLyricSpan> {
  if (words.isNullOrEmpty()) return emptyList()
  var cursor = 0
  var previousTime = -1.0
  val characters = BreakIterator.getCharacterInstance(Locale.ROOT).apply { setText(text) }
  val result = ArrayList<TimedLyricSpan>(words.size)
  for (word in words) {
    if (
      word.text.isBlank() ||
        !word.startTime.isFinite() ||
        !word.endTime.isFinite() ||
        word.startTime < 0 ||
        word.endTime < word.startTime ||
        word.startTime < previousTime
    ) {
      return emptyList()
    }
    val start = text.indexOf(word.text, cursor)
    if (start < 0) return emptyList()
    val end = start + word.text.length
    if (!safeLyricBoundary(text, start, characters) || !safeLyricBoundary(text, end, characters)) {
      return emptyList()
    }
    // A partial syllable/token match must not color an unrelated portion of the source line.
    if (text.substring(cursor, start).any { it.isLetterOrDigit() }) return emptyList()
    cursor = end
    result += TimedLyricSpan(start, cursor, (word.startTime * 1000).toLong())
    previousTime = word.startTime
  }
  if (text.substring(cursor).any { it.isLetterOrDigit() }) return emptyList()
  return result
}

private fun safeLyricBoundary(text: String, offset: Int, characters: BreakIterator): Boolean {
  if (offset == 0 || offset == text.length) return true
  if (!characters.isBoundary(offset)) return false
  if (Character.isHighSurrogate(text[offset - 1]) && Character.isLowSurrogate(text[offset]))
    return false
  val next = text.codePointAt(offset)
  val previous = text.codePointBefore(offset)
  val nextType = Character.getType(next)
  if (
    nextType == Character.NON_SPACING_MARK.toInt() ||
      nextType == Character.COMBINING_SPACING_MARK.toInt() ||
      nextType == Character.ENCLOSING_MARK.toInt() ||
      next == 0x200D ||
      next == 0x200C ||
      previous == 0x200D ||
      previous == 0x200C ||
      next in 0x1F3FB..0x1F3FF
  )
    return false
  // Be conservative across Java/Android grapheme implementations for Indic conjuncts.
  val previousName = Character.getName(previous).orEmpty()
  if (previousName.contains("VIRAMA") || previousName.contains("HALANT")) return false
  if (previous in 0x1F1E6..0x1F1FF && next in 0x1F1E6..0x1F1FF) return false
  return true
}

internal fun parseLyricsPresentation(value: String?): LyricsPresentationStyle =
  LyricsPresentationStyle.entries.firstOrNull { it.name == value }
    ?: LyricsPresentationStyle.CLASSIC_FLOW

/** No lyric-provider dependency: late network results cannot switch a style mid-song. */
internal fun nextAutoLyricsPresentation(
  trackId: String,
  previous: LyricsPresentationStyle,
): LyricsPresentationStyle {
  val suitable =
    listOf(
      LyricsPresentationStyle.CLASSIC_FLOW,
      LyricsPresentationStyle.FLOATING,
      LyricsPresentationStyle.CINEMATIC,
      LyricsPresentationStyle.MINIMAL_FOCUS,
    )
  val previousIndex = suitable.indexOf(previous).coerceAtLeast(0)
  val step = 1 + ((trackId.hashCode().toLong() and 0x7fffffffL) % (suitable.size - 1)).toInt()
  return suitable[(previousIndex + step) % suitable.size]
}

internal fun resolveLyricsPresentation(
  manual: LyricsPresentationStyle,
  auto: Boolean,
  trackId: String?,
  savedTrackId: String?,
  savedStyle: LyricsPresentationStyle,
): LyricsPresentationStyle =
  if (auto && !trackId.isNullOrBlank() && trackId == savedTrackId) savedStyle else manual

internal fun lyricEntranceProgress(
  positionMs: Long,
  lineTimeMs: Long,
  reducedMotion: Boolean,
): Float {
  if (reducedMotion) return 1f
  val progress = ((positionMs - lineTimeMs).toFloat() / 480f).coerceIn(0f, 1f)
  return progress * progress * (3f - 2f * progress)
}
