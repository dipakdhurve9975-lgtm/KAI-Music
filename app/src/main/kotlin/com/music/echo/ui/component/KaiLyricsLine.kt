/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.constants.LyricsPresentationStyle
import echo.music.iad1tya.lyrics.LyricsEntry
import echo.music.iad1tya.ui.screens.settings.LyricsPosition
import kotlin.math.abs

/**
 * Motion comes from the shared audio timeline. Pausing freezes it; seeking never restarts a timer.
 */
@Composable
internal fun KaiLyricsLine(
  entry: LyricsEntry,
  style: LyricsPresentationStyle,
  position: State<Long>,
  lyricsOffsetMs: Long,
  currentLineTimeMs: Long,
  nextLineTimeMs: Long?,
  distance: Int,
  active: Boolean,
  synced: Boolean,
  previewing: Boolean,
  reducedMotion: Boolean,
  textPosition: LyricsPosition,
  textColor: Color,
  textSize: Float,
  typeface: FontFamily,
  glow: Boolean,
  lineSpacing: Float,
  romanizeAsMain: Boolean,
  showTranslated: Boolean,
  selected: Boolean,
  selectionMode: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
) {
  val romanized by entry.romanizedTextFlow.collectAsState()
  val translated by entry.translatedTextFlow.collectAsState()
  val primary = if (romanizeAsMain && !romanized.isNullOrBlank()) romanized!! else entry.text
  val secondary = if (primary != entry.text) entry.text else romanized
  val spans =
    remember(entry.text, entry.words, primary) {
      if (primary == entry.text) sourceLyricSpans(entry.text, entry.words) else emptyList()
    }
  val alignment =
    when {
      entry.isBackground || entry.agent == "v1000" -> TextAlign.Center
      entry.agent == "v1" -> TextAlign.Left
      entry.agent == "v2" -> TextAlign.Right
      textPosition == LyricsPosition.CENTER -> TextAlign.Center
      textPosition == LyricsPosition.RIGHT -> TextAlign.Right
      else -> TextAlign.Left
    }
  val horizontal =
    when (alignment) {
      TextAlign.Center -> Alignment.CenterHorizontally
      TextAlign.Right -> Alignment.End
      else -> Alignment.Start
    }
  val focus = active || !synced || selectionMode || previewing
  val fontSize =
    textSize *
      when (style) {
        LyricsPresentationStyle.CINEMATIC -> 1.18f
        LyricsPresentationStyle.MINIMAL_FOCUS -> 1.06f
        else -> 1f
      } *
      if (entry.isBackground) .88f else 1f
  val density = LocalDensity.current
  val rowPadding =
    when (style) {
      LyricsPresentationStyle.CINEMATIC -> 18.dp
      LyricsPresentationStyle.MINIMAL_FOCUS -> 16.dp
      LyricsPresentationStyle.FLOATING -> 13.dp
      else -> 10.dp
    }
  Column(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .background(
          if (selectionMode && selected) MaterialTheme.colorScheme.primary.copy(alpha = .22f)
          else Color.Transparent
        )
        .padding(horizontal = 24.dp, vertical = rowPadding)
        .graphicsLayer {
          val positionMs = position.value + lyricsOffsetMs
          val entrance = lyricEntranceProgress(positionMs, entry.time, reducedMotion)
          val elapsedAfterFocus = (positionMs - currentLineTimeMs).coerceAtLeast(0L)
          val departure = lyricEntranceProgress(elapsedAfterFocus, 0L, reducedMotion)
          val near = abs(distance) <= 1
          val alpha =
            when {
              !synced || selectionMode || previewing -> 1f
              active ->
                if (style == LyricsPresentationStyle.CINEMATIC) .72f + .28f * entrance else 1f
              style == LyricsPresentationStyle.MINIMAL_FOCUS -> if (near) .3f else .17f
              style == LyricsPresentationStyle.CINEMATIC -> if (near) .30f else .12f
              style == LyricsPresentationStyle.FLOATING ->
                if (distance < 0) (.5f - .2f * departure).coerceAtLeast(.22f)
                else if (near) .48f else .28f
              else -> if (near) .52f else .34f
            }
          val scale =
            if (reducedMotion || !synced || selectionMode || previewing) 1f
            else
              when (style) {
                LyricsPresentationStyle.FLOATING -> if (active) .97f + .03f * entrance else .97f
                LyricsPresentationStyle.KINETIC -> if (active) .985f + .015f * entrance else .985f
                else -> 1f
              }
          val travelDp =
            if (reducedMotion || !synced || selectionMode || previewing) 0f
            else
              when (style) {
                LyricsPresentationStyle.FLOATING ->
                  if (active) 14f * (1f - entrance) else if (distance < 0) -7f * departure else 8f
                LyricsPresentationStyle.CINEMATIC -> if (active) 10f * (1f - entrance) else 0f
                LyricsPresentationStyle.KINETIC -> if (active) 3f * (1f - entrance) else 0f
                else -> 0f
              }

          val exitAlpha =
            if (
              active &&
                style == LyricsPresentationStyle.CINEMATIC &&
                !reducedMotion &&
                nextLineTimeMs != null &&
                nextLineTimeMs > entry.time
            ) {
              .82f + .18f * ((nextLineTimeMs - positionMs) / 240f).coerceIn(0f, 1f)
            } else 1f
          this.alpha = alpha * exitAlpha
          scaleX = scale
          scaleY = scale
          transformOrigin =
            TransformOrigin(
              if (alignment == TextAlign.Right) 1f
              else if (alignment == TextAlign.Center) .5f else 0f,
              .5f,
            )
          translationY = travelDp * density.density
          translationX =
            if (style == LyricsPresentationStyle.KINETIC && active && !reducedMotion)
              (1f - entrance) * 3f * density.density
            else 0f
        },
    horizontalAlignment = horizontal,
  ) {
    val wordReveal =
      if (style == LyricsPresentationStyle.KINETIC && active && synced && spans.isNotEmpty()) {
        val levels by
          remember(position, spans, lyricsOffsetMs, reducedMotion) {
            derivedStateOf {
              val now = position.value + lyricsOffsetMs
              spans.map { span ->
                if (reducedMotion) {
                  if (now >= span.startTimeMs) 4 else 0
                } else ((now - span.startTimeMs) / 25L).toInt().coerceIn(0, 4)
              }
            }
          }
        levels
      } else emptyList()
    val displayText =
      remember(primary, spans, wordReveal, textColor) {
        buildAnnotatedString {
          append(primary)
          spans.forEachIndexed { index, span ->
            if (wordReveal.isNotEmpty()) {
              addStyle(
                SpanStyle(color = textColor.copy(alpha = .42f + .58f * wordReveal[index] / 4f)),
                span.start,
                span.end,
              )
            }
          }
        }
      }

    Text(
      text = displayText,
      style =
        MaterialTheme.typography.headlineMedium.copy(
          fontSize = fontSize.sp,
          fontFamily = typeface,
          shadow = lyricGlowShadow(textColor, glow && active),
          lineHeight = (fontSize * lineSpacing.coerceIn(1.1f, 1.5f)).sp,
          fontWeight = if (focus) FontWeight.Bold else FontWeight.SemiBold,
        ),
      textAlign = alignment,
      color = textColor,
      modifier = Modifier.fillMaxWidth(),
    )
    if (!secondary.isNullOrBlank() && secondary != primary) {
      Text(
        text = secondary,
        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = typeface),
        fontSize = (textSize * .64f).sp,
        lineHeight = (textSize * .86f).sp,
        textAlign = alignment,
        color = textColor.copy(alpha = if (focus) .76f else .8f),
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
      )
    }
    if (
      showTranslated &&
        !translated.isNullOrBlank() &&
        translated != primary &&
        translated != secondary
    ) {
      Text(
        text = translated!!,
        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = typeface),
        fontSize = (textSize * .6f).sp,
        lineHeight = (textSize * .82f).sp,
        textAlign = alignment,
        color = textColor.copy(alpha = .74f),
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
      )
    }
  }
}
