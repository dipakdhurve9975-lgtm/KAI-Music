package com.music.lrclib.models

import org.junit.Assert.*
import org.junit.Test

class TrackMatchingTest {
  private fun song(id: Int, title: String, artist: String, duration: Double = 180.0) =
    Track(id, title, artist, duration, "plain", null)

  @Test
  fun sameDurationDoesNotAcceptAnotherTitleOrArtist() {
    assertNull(
      listOf(song(1, "Another song", "Someone else")).bestMatchingFor(180, "Our song", "Our artist")
    )
    assertNull(
      listOf(song(1, "Our song", "Someone else")).bestMatchingFor(180, "Our song", "Our artist")
    )
  }

  @Test
  fun matchingMetadataAndDurationAreBothRequired() {
    val right = song(2, "Our song", "Our artist", 182.0)
    assertEquals(
      right,
      listOf(song(1, "Wrong", "Wrong"), right).bestMatchingFor(180, "Our song", "Our artist"),
    )
    assertNull(listOf(right.copy(duration = 200.0)).bestMatchingFor(180, "Our song", "Our artist"))
  }

  @Test
  fun scriptAndPunctuationAreNormalizedWithoutErasingLetters() {
    val right = song(1, "नमस्ते दुनिया", "कलाकार")
    assertEquals(right, listOf(right).bestMatchingFor(180, "नमस्ते-दुनिया", "कलाकार"))
  }

  @Test
  fun unknownDurationStillRequiresMetadata() {
    assertNull(listOf(song(1, "Wrong", "Wrong")).bestMatchingFor(-1, "Our song", "Our artist"))
  }
}
