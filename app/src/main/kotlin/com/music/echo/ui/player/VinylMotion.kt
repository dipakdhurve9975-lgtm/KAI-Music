/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

/** 33 1/3 RPM = 200 degrees/second. Only visible, playing frame deltas enter here. */
internal fun advanceVinylAngle(angle: Float, elapsedNanos: Long): Float =
  ((angle + elapsedNanos.coerceAtLeast(0L) / 1_000_000_000.0 * 200.0) % 360.0).toFloat()
