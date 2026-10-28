/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KaiNavigationPolicyTest {
  private val roots = setOf("home", "search_input", "library")

  @Test
  fun `launcher shortcut actions resolve to their labeled destinations`() {
    assertEquals("search_input", kaiShortcutRoot("echo.music.iad1tya.action.SEARCH"))
    assertEquals("library", kaiShortcutRoot("echo.music.iad1tya.action.LIBRARY"))
    assertEquals(null, kaiShortcutRoot("android.intent.action.MAIN"))
    assertEquals(null, kaiShortcutRoot(null))
  }

  @Test
  fun `root destinations override stale restored owners`() {
    listOf("home", "search_input", "library", "recognition").forEach { route ->
      assertEquals(
        route,
        resolveKaiNavigationOwner(route, "library", "search_input", "home", roots),
      )
    }
  }

  @Test
  fun `search results select Search even when reached programmatically from Home`() {
    assertEquals(
      "search_input",
      resolveKaiNavigationOwner("search/{query}", "home", "home", "home", roots),
    )
    assertTrue(shouldShowKaiNavigation("search/{query}", roots))
  }

  @Test
  fun `saved detail owner survives Back from another tab and restoration`() {
    assertEquals(
      "library",
      resolveKaiNavigationOwner("album/{albumId}", "library", "search_input", "home", roots),
    )
    assertEquals(
      "search_input",
      resolveKaiNavigationOwner(
        "artist/{artistId}/songs",
        "search_input",
        "library",
        "home",
        roots,
      ),
    )
  }

  @Test
  fun `new detail inherits previous entry owner rather than a separate tab index`() {
    assertEquals(
      "library",
      resolveKaiNavigationOwner("local_playlist/{playlistId}", null, "library", "home", roots),
    )
    assertEquals(
      "recognition",
      resolveKaiNavigationOwner("album/{albumId}", null, "recognition", "home", roots),
    )
  }

  @Test
  fun `direct detail deep link falls back to actual start destination`() {
    assertEquals(
      "library",
      resolveKaiNavigationOwner("album/{albumId}", null, null, "library", roots),
    )
    assertEquals(
      "home",
      resolveKaiNavigationOwner("artist/{artistId}", "removed-tab", "obsolete", "unknown", roots),
    )
  }

  @Test
  fun `optional Listen Together owner is retained only when its tab exists`() {
    assertEquals(
      "home",
      resolveKaiNavigationOwner("album/a", "listen_together", null, "home", roots),
    )
    assertEquals(
      "listen_together",
      resolveKaiNavigationOwner(
        "album/a",
        "listen_together",
        null,
        "home",
        roots + "listen_together",
      ),
    )
  }

  @Test
  fun `recognition and history retain navigation and recognition selection`() {
    listOf("recognition", "recognition_history").forEach { route ->
      assertTrue(shouldShowKaiNavigation(route, roots))
      assertEquals("recognition", resolveKaiNavigationOwner(route, null, "home", "home", roots))
    }
  }

  @Test
  fun `all Library collection detail routes preserve dock visibility`() {
    listOf(
        "album/a",
        "artist/a",
        "online_playlist/a",
        "local_playlist/a",
        "spotify_playlist/a",
        "auto_playlist/liked",
        "cache_playlist/cached",
        "top_playlist/50",
        "bottom_playlist/50",
        "local_songs",
      )
      .forEach {
        assertTrue(it, shouldShowKaiNavigation(it, roots))
      }
  }

  @Test
  fun `immersive and settings screens retain their separate chrome`() {
    listOf(
        "ambient_mode",
        "ambient_settings",
        "music_flow",
        "settings",
        "settings/about?highlightKey={highlightKey}",
        "update",
        "listen_together/chat",
      )
      .forEach {
        assertFalse(it, shouldShowKaiNavigation(it, roots))
      }
  }

  @Test
  fun `exact root reselect differs from tapping owner while in detail`() {
    assertEquals(
      KaiRootSelectionAction.RESELECT_ROOT,
      kaiRootSelectionAction("library", "library", "library"),
    )
    assertEquals(
      KaiRootSelectionAction.RETURN_TO_ROOT,
      kaiRootSelectionAction("album/a", "library", "library"),
    )
    assertEquals(
      KaiRootSelectionAction.RETURN_TO_ROOT,
      kaiRootSelectionAction("search/{query}", "search_input", "search_input"),
    )
    assertEquals(
      KaiRootSelectionAction.SWITCH_ROOT,
      kaiRootSelectionAction("album/a", "library", "home"),
    )
  }

  @Test
  fun `repeated taps are resolved against changed live route`() {
    assertEquals(
      KaiRootSelectionAction.SWITCH_ROOT,
      kaiRootSelectionAction("home", "home", "library"),
    )
    assertEquals(
      KaiRootSelectionAction.RESELECT_ROOT,
      kaiRootSelectionAction("library", "library", "library"),
    )
  }

  @Test
  fun `normal dock reserves navigation bottom gap mini gap and system inset once`() {
    assertEquals(180f, kaiDockBottomInset(24f, 72f, 12f, 64f, 8f, true, true))
    assertEquals(108f, kaiDockBottomInset(24f, 72f, 12f, 64f, 8f, true, false))
  }

  @Test
  fun `alternate dock reserves expanded max size while inline scroll geometry changes`() {
    assertEquals(176f, kaiDockBottomInset(24f, 72f, 8f, 64f, 8f, true, true))
  }

  @Test
  fun `rail and hidden chrome do not reserve a bottom navigation bar`() {
    assertEquals(96f, kaiDockBottomInset(24f, 72f, 12f, 64f, 8f, false, true))
    assertEquals(24f, kaiDockBottomInset(24f, 72f, 12f, 64f, 8f, false, false))
  }
}
