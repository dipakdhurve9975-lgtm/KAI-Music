package com.music.youlyplus

import com.music.youlyplus.YouLyPlus.convertToLrc
import com.music.youlyplus.models.LyricsItem
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
}
