/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens

internal enum class HomeLoadingPresentation {
  NONE,
  INITIAL,
  PAGINATION,
}

/** Cached content stays visible during refresh; an idle continuation is never a loading state. */
internal fun homeLoadingPresentation(
  hasContent: Boolean,
  initialLoading: Boolean,
  paginationLoading: Boolean,
): HomeLoadingPresentation =
  when {
    !hasContent && initialLoading -> HomeLoadingPresentation.INITIAL
    hasContent && paginationLoading -> HomeLoadingPresentation.PAGINATION
    else -> HomeLoadingPresentation.NONE
  }

/** Length prefixes retain stable shelf identities without hash or separator collisions. */
internal fun homeShelfIdentity(
  title: String,
  label: String?,
  browseId: String?,
  params: String?,
): String =
  listOf(title, label.orEmpty(), browseId.orEmpty(), params.orEmpty()).joinToString("") {
    "${it.length}:$it"
  }
