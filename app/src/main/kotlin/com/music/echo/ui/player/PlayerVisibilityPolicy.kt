/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.player

/** A visible sheet may rest fractionally short of its expanded anchor after a drag or resize. */
internal fun isPlayerPresentationVisible(sheetProgress: Float): Boolean = sheetProgress > .5f
