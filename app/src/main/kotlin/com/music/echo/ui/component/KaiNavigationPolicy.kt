package echo.music.iad1tya.ui.component

internal const val KaiNavigationOwnerKey = "kai_navigation_owner"

internal fun kaiShortcutRoot(action: String?): String? =
  when (action) {
    "echo.music.iad1tya.action.SEARCH" -> "search_input"
    "echo.music.iad1tya.action.LIBRARY" -> "library"
    else -> null
  }

private val kaiRootRoutes =
  setOf("home", "search_input", "recognition", "library", "listen_together")

/** Search results and recognition history belong to their actual navigation destination. */
internal fun directKaiNavigationOwner(route: String?): String? =
  when {
    route in kaiRootRoutes -> route
    route?.startsWith("search/") == true -> "search_input"
    route == "recognition_history" -> "recognition"
    else -> null
  }

/**
 * Persisted per entry: Back/restoration must not replace a detail's owner with the departing tab.
 */
internal fun resolveKaiNavigationOwner(
  route: String?,
  savedOwner: String?,
  previousOwner: String?,
  defaultRoot: String,
  availableRoots: Set<String> = kaiRootRoutes,
): String {
  val allowed = availableRoots + "recognition"
  return directKaiNavigationOwner(route)?.takeIf { it in allowed }
    ?: savedOwner?.takeIf { it in allowed }
    ?: previousOwner?.takeIf { it in allowed }
    ?: directKaiNavigationOwner(defaultRoot)?.takeIf { it in allowed }
    ?: "home"
}

/** Immersive/settings routes retain their existing chrome policy; recognition keeps the dock. */
internal fun shouldShowKaiNavigation(route: String?, availableRoots: Set<String>): Boolean =
  route == null ||
    route in availableRoots ||
    route == "recognition" ||
    route == "recognition_history" ||
    route.startsWith("search/") ||
    route == "local_songs" ||
    listOf(
        "album/",
        "online_playlist/",
        "local_playlist/",
        "spotify_playlist/",
        "artist/",
        "auto_playlist/",
        "cache_playlist/",
        "top_playlist/",
        "bottom_playlist/",
      )
      .any(route::startsWith)

internal enum class KaiRootSelectionAction {
  RESELECT_ROOT,
  RETURN_TO_ROOT,
  SWITCH_ROOT,
}

internal fun kaiRootSelectionAction(
  currentRoute: String?,
  currentOwner: String?,
  targetRoot: String,
): KaiRootSelectionAction =
  when {
    currentRoute == targetRoot -> KaiRootSelectionAction.RESELECT_ROOT
    currentOwner == targetRoot -> KaiRootSelectionAction.RETURN_TO_ROOT
    else -> KaiRootSelectionAction.SWITCH_ROOT
  }

/** Pixel-independent inset arithmetic, shared by the dock calculation and its regression tests. */
internal fun kaiDockBottomInset(
  systemBottom: Float,
  navigationHeight: Float,
  navigationBottomGap: Float,
  miniPlayerHeight: Float,
  miniPlayerGap: Float,
  showNavigation: Boolean,
  showMiniPlayer: Boolean,
): Float =
  systemBottom +
    (if (showNavigation) navigationHeight + navigationBottomGap else 0f) +
    (if (showMiniPlayer) miniPlayerHeight + miniPlayerGap else 0f)

/** Root switching is immediate; the dock owns its own small spring independently. */
internal fun kaiImmediateRootTransition(from: String?, to: String?): Boolean =
  from in kaiRootRoutes && to in kaiRootRoutes
