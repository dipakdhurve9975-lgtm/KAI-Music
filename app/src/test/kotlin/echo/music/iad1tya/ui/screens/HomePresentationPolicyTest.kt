/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class HomePresentationPolicyTest {
  @Test
  fun initialRequestWithoutContentShowsInitialPlaceholder() {
    assertEquals(HomeLoadingPresentation.INITIAL, homeLoadingPresentation(false, true, false))
  }

  @Test
  fun cachedContentRemainsVisibleDuringRefresh() {
    assertEquals(HomeLoadingPresentation.NONE, homeLoadingPresentation(true, true, false))
  }

  @Test
  fun anIdleContinuationDoesNotShowLoading() {
    assertEquals(HomeLoadingPresentation.NONE, homeLoadingPresentation(true, false, false))
  }

  @Test
  fun realPaginationGetsOnlyTheCompactIndicator() {
    assertEquals(HomeLoadingPresentation.PAGINATION, homeLoadingPresentation(true, false, true))
  }

  @Test
  fun failedOrEmptyIdleContentNeverLeavesAnInfiniteSkeleton() {
    assertEquals(HomeLoadingPresentation.NONE, homeLoadingPresentation(false, false, false))
  }

  @Test
  fun shelfIdentitySurvivesReorderingAndDistinguishesEndpoints() {
    val first = homeShelfIdentity("New releases", null, "browse-a", "one")
    assertEquals(first, homeShelfIdentity("New releases", null, "browse-a", "one"))
    assertNotEquals(first, homeShelfIdentity("New releases", null, "browse-b", "one"))
    assertNotEquals(
      homeShelfIdentity("ab", "c", null, null),
      homeShelfIdentity("a", "bc", null, null),
    )
  }
}
