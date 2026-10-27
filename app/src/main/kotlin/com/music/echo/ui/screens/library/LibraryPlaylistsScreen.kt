package echo.music.iad1tya.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.music.innertube.utils.parseCookieString
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.CONTENT_TYPE_HEADER
import echo.music.iad1tya.constants.CONTENT_TYPE_PLAYLIST
import echo.music.iad1tya.constants.GridItemSize
import echo.music.iad1tya.constants.GridItemsSizeKey
import echo.music.iad1tya.constants.GridThumbnailHeight
import echo.music.iad1tya.constants.InnerTubeCookieKey
import echo.music.iad1tya.constants.LibraryViewType
import echo.music.iad1tya.constants.PlaylistSortDescendingKey
import echo.music.iad1tya.constants.PlaylistSortType
import echo.music.iad1tya.constants.PlaylistSortTypeKey
import echo.music.iad1tya.constants.PlaylistViewTypeKey
import echo.music.iad1tya.constants.ShowBottomPlaylistKey
import echo.music.iad1tya.constants.ShowCachedPlaylistKey
import echo.music.iad1tya.constants.ShowDownloadedPlaylistKey
import echo.music.iad1tya.constants.ShowExportedPlaylistKey
import echo.music.iad1tya.constants.ShowLikedPlaylistKey
import echo.music.iad1tya.constants.ShowTopPlaylistKey
import echo.music.iad1tya.constants.YtmSyncKey
import echo.music.iad1tya.ui.component.LibraryPlaylistGridItem
import echo.music.iad1tya.ui.component.LibraryPlaylistListItem
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.SortHeader
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.viewmodels.LibraryPlaylistsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun LibraryPlaylistsScreen(
  navController: NavController,
  filterContent: @Composable () -> Unit,
  viewModel: LibraryPlaylistsViewModel = hiltViewModel(),
  initialTextFieldValue: String? = null,
  allowSyncing: Boolean = true,
) {
  val menuState = LocalMenuState.current
  val haptic = LocalHapticFeedback.current

  val coroutineScope = rememberCoroutineScope()

  var viewType by rememberEnumPreference(PlaylistViewTypeKey, LibraryViewType.GRID)
  val (sortType, onSortTypeChange) =
    rememberEnumPreference(PlaylistSortTypeKey, PlaylistSortType.CREATE_DATE)
  val (sortDescending, onSortDescendingChange) = rememberPreference(PlaylistSortDescendingKey, true)
  val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

  val playlists by viewModel.allPlaylists.collectAsState()

  val topSize by viewModel.topValue.collectAsState(initial = "50")

  val (showLiked) = rememberPreference(ShowLikedPlaylistKey, true)
  val (showDownloaded) = rememberPreference(ShowDownloadedPlaylistKey, true)
  val (showExported) = rememberPreference(ShowExportedPlaylistKey, true)
  val (showTop) = rememberPreference(ShowTopPlaylistKey, true)
  val (showBottom) = rememberPreference(ShowBottomPlaylistKey, true)
  val (showCached) = rememberPreference(ShowCachedPlaylistKey, true)

  val lazyListState = rememberLazyListState()
  val lazyGridState = rememberLazyGridState()

  val backStackEntry by navController.currentBackStackEntryAsState()
  val scrollToTop =
    backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

  val (innerTubeCookie) = rememberPreference(InnerTubeCookieKey, "")
  val isLoggedIn = remember(innerTubeCookie) { "SAPISID" in parseCookieString(innerTubeCookie) }

  val (ytmSync) = rememberPreference(YtmSyncKey, true)

  LaunchedEffect(Unit) {
    if (ytmSync) {
      withContext(Dispatchers.IO) { viewModel.sync() }
    }
  }

  LaunchedEffect(scrollToTop?.value) {
    if (scrollToTop?.value == true) {
      when (viewType) {
        LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
        LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
      }
      backStackEntry?.savedStateHandle?.set("scrollToTop", false)
    }
  }
  val headerContent =
    @Composable {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 24.dp),
      ) {
        SortHeader(
          sortType = sortType,
          sortDescending = sortDescending,
          onSortTypeChange = onSortTypeChange,
          onSortDescendingChange = onSortDescendingChange,
          sortTypeText = { sortType ->
            when (sortType) {
              PlaylistSortType.CREATE_DATE -> R.string.sort_by_create_date
              PlaylistSortType.NAME -> R.string.sort_by_name
              PlaylistSortType.SONG_COUNT -> R.string.sort_by_song_count
              PlaylistSortType.LAST_UPDATED -> R.string.sort_by_last_updated
            }
          },
        )

        Spacer(Modifier.weight(1f))

        Text(
          text = pluralStringResource(R.plurals.n_playlist, playlists.size, playlists.size),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.width(16.dp))
      }
    }

  Box(modifier = Modifier.fillMaxSize()) {
    when (viewType) {
      LibraryViewType.LIST -> {
        LazyColumn(
          state = lazyListState,
          contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
          item(
            key = "filter",
            contentType = CONTENT_TYPE_HEADER,
          ) {
            filterContent()
          }

          item(
            key = "auto_playlists_grid",
            contentType = CONTENT_TYPE_HEADER,
          ) {
            LibraryCollections(
              navController,
              topSize,
              showLiked,
              showDownloaded,
              showExported,
              showCached,
              showTop,
              showBottom,
              showTasteMix = true,
            )
          }

          item(
            key = "header",
            contentType = CONTENT_TYPE_HEADER,
          ) {
            headerContent()
          }

          item(
            key = "playlists_header",
            contentType = CONTENT_TYPE_HEADER,
          ) {
            androidx.compose.material3.Text(
              text = "Playlists",
              style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            )
          }

          playlists.let { playlists ->
            if (playlists.isEmpty()) {
              item(key = "empty_placeholder") {}
            }

            items(
              items = playlists.distinctBy { it.id },
              key = { it.id },
              contentType = { CONTENT_TYPE_PLAYLIST },
            ) { playlist ->
              LibraryPlaylistListItem(
                navController = navController,
                menuState = menuState,
                coroutineScope = coroutineScope,
                playlist = playlist,
                modifier = Modifier,
              )
            }
          }
        }
      }
      LibraryViewType.GRID -> {
        LazyVerticalGrid(
          state = lazyGridState,
          columns =
            GridCells.Adaptive(
              minSize =
                GridThumbnailHeight + if (gridItemSize == GridItemSize.BIG) 24.dp else (-24).dp
            ),
          contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
          item(
            key = "filter",
            span = { GridItemSpan(maxLineSpan) },
            contentType = CONTENT_TYPE_HEADER,
          ) {
            filterContent()
          }

          item(
            key = "auto_playlists_grid",
            span = { GridItemSpan(maxLineSpan) },
            contentType = CONTENT_TYPE_HEADER,
          ) {
            LibraryCollections(
              navController,
              topSize,
              showLiked,
              showDownloaded,
              showExported,
              showCached,
              showTop,
              showBottom,
              showTasteMix = true,
            )
          }

          item(
            key = "header",
            span = { GridItemSpan(maxLineSpan) },
            contentType = CONTENT_TYPE_HEADER,
          ) {
            headerContent()
          }

          item(
            key = "playlists_header",
            span = { GridItemSpan(maxLineSpan) },
            contentType = CONTENT_TYPE_HEADER,
          ) {
            androidx.compose.material3.Text(
              text = "Playlists",
              style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            )
          }

          playlists.let { playlists ->
            if (playlists.isEmpty()) {
              item(span = { GridItemSpan(maxLineSpan) }) {}
            }

            items(
              items = playlists.distinctBy { it.id },
              key = { it.id },
              contentType = { CONTENT_TYPE_PLAYLIST },
            ) { playlist ->
              LibraryPlaylistGridItem(
                navController = navController,
                menuState = menuState,
                coroutineScope = coroutineScope,
                playlist = playlist,
                modifier = Modifier,
              )
            }
          }
        }
      }
    }
  }
}
