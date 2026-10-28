/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package com.music.youlyplus.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * Response model for the LyricsPlus / KPoe API (/v2/lyrics/get). Fields mirror what YouLyPlus
 * extension parses from the backend.
 */
@Serializable
data class LyricsResponse(
  // LRCLib style
  val id: Int? = null,
  val syncedLyrics: String? = null,
  val plainLyrics: String? = null,

  // KPoe style (array of lines)
  val lyrics: List<LyricsItem>? = null,
  val type: String? = null,

  // Common metadata
  val trackName: String? = null,
  val artistName: String? = null,
  val albumName: String? = null,
  val duration: Double? = null,
  val metadata: LyricsMetadata? = null,
)

@Serializable
data class LyricsItem(
  val text: String? = null,
  val time: Long? = null, // milliseconds
  val duration: Long? = null, // milliseconds
  val syllabus: List<Syllable>? = null,
)

@Serializable
data class Syllable(
  val text: String? = null,
  val time: Long? = null, // milliseconds
  val duration: Long? = null, // milliseconds
  val isBackground: Boolean? = null,
)

@Serializable data class LyricsMetadata(val totalDuration: JsonPrimitive? = null)
