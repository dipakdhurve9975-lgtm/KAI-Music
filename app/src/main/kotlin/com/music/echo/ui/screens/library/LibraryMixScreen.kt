/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AlbumViewTypeKey
import echo.music.iad1tya.constants.CONTENT_TYPE_HEADER
import echo.music.iad1tya.constants.CONTENT_TYPE_PLAYLIST
import echo.music.iad1tya.constants.GridItemSize
import echo.music.iad1tya.constants.GridItemsSizeKey
import echo.music.iad1tya.constants.GridThumbnailHeight
import echo.music.iad1tya.constants.LibraryViewType
import echo.music.iad1tya.constants.MixSortDescendingKey
import echo.music.iad1tya.constants.MixSortType
import echo.music.iad1tya.constants.MixSortTypeKey
import echo.music.iad1tya.constants.ShowBottomPlaylistKey
import echo.music.iad1tya.constants.ShowCachedPlaylistKey
import echo.music.iad1tya.constants.ShowDownloadedPlaylistKey
import echo.music.iad1tya.constants.ShowExportedPlaylistKey
import echo.music.iad1tya.constants.ShowLikedPlaylistKey
import echo.music.iad1tya.constants.ShowTopPlaylistKey
import echo.music.iad1tya.constants.YtmSyncKey
import echo.music.iad1tya.db.entities.Album
import echo.music.iad1tya.db.entities.Artist
import echo.music.iad1tya.db.entities.Playlist
import echo.music.iad1tya.extensions.reversed
import echo.music.iad1tya.ui.component.AlbumGridItem
import echo.music.iad1tya.ui.component.AlbumListItem
import echo.music.iad1tya.ui.component.ArtistGridItem
import echo.music.iad1tya.ui.component.ArtistListItem
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.PlaylistGridItem
import echo.music.iad1tya.ui.component.PlaylistListItem
import echo.music.iad1tya.ui.component.SortHeader
import echo.music.iad1tya.ui.menu.AlbumMenu
import echo.music.iad1tya.ui.menu.ArtistMenu
import echo.music.iad1tya.ui.menu.PlaylistMenu
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.viewmodels.LibraryMixViewModel
import java.text.Collator
import java.time.LocalDateTime
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun LibraryMixScreen(
  navController: NavController,
  filterContent: @Composable () -> Unit,
  viewModel: LibraryMixViewModel = hiltViewModel(),
) {
  val menuState = LocalMenuState.current
  val haptic = LocalHapticFeedback.current
  val playerConnection = LocalPlayerConnection.current ?: return
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  var viewType by rememberEnumPreference(AlbumViewTypeKey, LibraryViewType.GRID)
  val (sortType, onSortTypeChange) = rememberEnumPreference(MixSortTypeKey, MixSortType.CREATE_DATE)
  val (sortDescending, onSortDescendingChange) = rememberPreference(MixSortDescendingKey, true)
  val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

  val (ytmSync) = rememberPreference(YtmSyncKey, true)

  val topSize by viewModel.topValue.collectAsState(initial = "50")
  val (showLiked) = rememberPreference(ShowLikedPlaylistKey, true)
  val (showDownloaded) = rememberPreference(ShowDownloadedPlaylistKey, true)
  val (showExported) = rememberPreference(ShowExportedPlaylistKey, true)
  val (showTop) = rememberPreference(ShowTopPlaylistKey, true)
  val (showBottom) = rememberPreference(ShowBottomPlaylistKey, true)
  val (showCached) = rememberPreference(ShowCachedPlaylistKey, true)

  val albums = viewModel.albums.collectAsState()
  val artist = viewModel.artists.collectAsState()
  val playlist = viewModel.playlists.collectAsState()

  val collator =
    remember(Locale.getDefault()) {
      Collator.getInstance(Locale.getDefault()).apply { strength = Collator.PRIMARY }
    }

  fun <T> List<T>.sortItems(): List<T> {
    return when (sortType) {
      MixSortType.CREATE_DATE ->
        this.sortedBy { item ->
          when (item) {
            is Album -> item.album.bookmarkedAt
            is Artist -> item.artist.bookmarkedAt
            is Playlist -> item.playlist.createdAt
            else -> LocalDateTime.now()
          }
        }
      MixSortType.NAME ->
        this.sortedWith(
          compareBy(collator) { item ->
            when (item) {
              is Album -> item.album.title
              is Artist -> item.artist.name
              is Playlist -> item.playlist.name
              else -> ""
            }
          }
        )
      MixSortType.LAST_UPDATED ->
        this.sortedBy { item ->
          when (item) {
            is Album -> item.album.lastUpdateTime
            is Artist -> item.artist.lastUpdateTime
            is Playlist -> item.playlist.lastUpdateTime
            else -> LocalDateTime.now()
          }
        }
    }.reversed(sortDescending)
  }

  val sortedPlaylists =
    remember(playlist.value, sortType, sortDescending, collator) { playlist.value.sortItems() }
  val pinnedPlaylists =
    remember(sortedPlaylists) { sortedPlaylists.filter { it.playlist.isPinned } }
  val otherPlaylists =
    remember(sortedPlaylists) { sortedPlaylists.filterNot { it.playlist.isPinned } }
  val sortedArtists =
    remember(artist.value, sortType, sortDescending, collator) { artist.value.sortItems() }
  val sortedAlbums =
    remember(albums.value, sortType, sortDescending, collator) { albums.value.sortItems() }

  val coroutineScope = rememberCoroutineScope()

  val lazyListState = rememberLazyListState()
  val lazyGridState = rememberLazyGridState()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val scrollToTop =
    backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

  LaunchedEffect(scrollToTop?.value) {
    if (scrollToTop?.value == true) {
      when (viewType) {
        LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
        LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
      }
      backStackEntry?.savedStateHandle?.set("scrollToTop", false)
    }
  }

  LaunchedEffect(Unit) {
    if (ytmSync) {
      withContext(Dispatchers.IO) { viewModel.syncAllLibrary() }
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
              MixSortType.CREATE_DATE -> R.string.sort_by_create_date
              MixSortType.LAST_UPDATED -> R.string.sort_by_last_updated
              MixSortType.NAME -> R.string.sort_by_name
            }
          },
        )

        Spacer(Modifier.weight(1f))

        Spacer(modifier = Modifier.width(16.dp))
      }
    }

  val isRefreshing by viewModel.isRefreshing.collectAsState()
  val pullRefreshState = rememberPullToRefreshState()

  PullToRefreshBox(
    state = pullRefreshState,
    isRefreshing = isRefreshing,
    onRefresh = viewModel::refresh,
    indicator = {
      PullToRefreshDefaults.LoadingIndicator(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        modifier = Modifier.align(Alignment.TopCenter),
      )
    },
  ) {
    when (viewType) {
      LibraryViewType.LIST ->
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
              showTasteMix = false,
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
              text = stringResource(R.string.filter_playlists),
              style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            )
          }

          val allPlaylists = pinnedPlaylists + otherPlaylists
          items(
            items = allPlaylists.distinctBy { it.id },
            key = { "playlist_${it.id}" },
            contentType = { CONTENT_TYPE_PLAYLIST },
          ) { item ->
            PlaylistListItem(
              playlist = item,
              trailingContent = {
                IconButton(
                  onClick = {
                    menuState.show {
                      PlaylistMenu(
                        playlist = item,
                        coroutineScope = coroutineScope,
                        onDismiss = menuState::dismiss,
                      )
                    }
                  }
                ) {
                  Icon(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = null,
                  )
                }
              },
              modifier =
                Modifier.fillMaxWidth()
                  .combinedClickable(
                    onClick = { navController.navigate("local_playlist/${item.id}") },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        PlaylistMenu(
                          playlist = item,
                          coroutineScope = coroutineScope,
                          onDismiss = menuState::dismiss,
                        )
                      }
                    },
                  ),
            )
          }

          if (sortedArtists.isNotEmpty()) {
            item(
              key = "artists_header",
              contentType = CONTENT_TYPE_HEADER,
            ) {
              androidx.compose.material3.Text(
                text = stringResource(R.string.filter_artists),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
              )
            }

            items(
              items = sortedArtists.distinctBy { it.id },
              key = { "artist_${it.id}" },
              contentType = { "artist" },
            ) { item ->
              ArtistListItem(
                artist = item,
                trailingContent = {
                  IconButton(
                    onClick = {
                      menuState.show {
                        ArtistMenu(
                          originalArtist = item,
                          coroutineScope = coroutineScope,
                          onDismiss = menuState::dismiss,
                        )
                      }
                    }
                  ) {
                    Icon(
                      painter = painterResource(R.drawable.more_vert),
                      contentDescription = null,
                    )
                  }
                },
                modifier =
                  Modifier.fillMaxWidth()
                    .combinedClickable(
                      onClick = { navController.navigate("artist/${item.id}") },
                      onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                          ArtistMenu(
                            originalArtist = item,
                            coroutineScope = coroutineScope,
                            onDismiss = menuState::dismiss,
                          )
                        }
                      },
                    ),
              )
            }
          }

          if (sortedAlbums.isNotEmpty()) {
            item(
              key = "albums_header",
              contentType = CONTENT_TYPE_HEADER,
            ) {
              androidx.compose.material3.Text(
                text = stringResource(R.string.filter_albums),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
              )
            }

            items(
              items = sortedAlbums.distinctBy { it.id },
              key = { "album_${it.id}" },
              contentType = { "album" },
            ) { item ->
              AlbumListItem(
                album = item,
                isActive = item.id == mediaMetadata?.album?.id,
                isPlaying = isPlaying,
                trailingContent = {
                  IconButton(
                    onClick = {
                      menuState.show {
                        AlbumMenu(
                          originalAlbum = item,
                          navController = navController,
                          onDismiss = menuState::dismiss,
                        )
                      }
                    }
                  ) {
                    Icon(
                      painter = painterResource(R.drawable.more_vert),
                      contentDescription = null,
                    )
                  }
                },
                modifier =
                  Modifier.fillMaxWidth()
                    .combinedClickable(
                      onClick = { navController.navigate("album/${item.id}") },
                      onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                          AlbumMenu(
                            originalAlbum = item,
                            navController = navController,
                            onDismiss = menuState::dismiss,
                          )
                        }
                      },
                    ),
              )
            }
          }
        }
      LibraryViewType.GRID ->
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
              showTasteMix = false,
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
              text = stringResource(R.string.filter_playlists),
              style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            )
          }

          val allPlaylists = pinnedPlaylists + otherPlaylists
          items(
            items = allPlaylists.distinctBy { it.id },
            key = { "playlist_${it.id}" },
            contentType = { CONTENT_TYPE_PLAYLIST },
          ) { item ->
            PlaylistGridItem(
              playlist = item,
              fillMaxWidth = true,
              modifier =
                Modifier.fillMaxWidth()
                  .combinedClickable(
                    onClick = { navController.navigate("local_playlist/${item.id}") },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        PlaylistMenu(
                          playlist = item,
                          coroutineScope = coroutineScope,
                          onDismiss = menuState::dismiss,
                        )
                      }
                    },
                  ),
            )
          }

          if (sortedArtists.isNotEmpty()) {
            item(
              key = "artists_header",
              span = { GridItemSpan(maxLineSpan) },
              contentType = CONTENT_TYPE_HEADER,
            ) {
              androidx.compose.material3.Text(
                text = stringResource(R.string.filter_artists),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
              )
            }

            items(
              items = sortedArtists.distinctBy { it.id },
              key = { "artist_${it.id}" },
              contentType = { "artist" },
            ) { item ->
              ArtistGridItem(
                artist = item,
                fillMaxWidth = true,
                modifier =
                  Modifier.fillMaxWidth()
                    .combinedClickable(
                      onClick = { navController.navigate("artist/${item.id}") },
                      onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                          ArtistMenu(
                            originalArtist = item,
                            coroutineScope = coroutineScope,
                            onDismiss = menuState::dismiss,
                          )
                        }
                      },
                    ),
              )
            }
          }

          if (sortedAlbums.isNotEmpty()) {
            item(
              key = "albums_header",
              span = { GridItemSpan(maxLineSpan) },
              contentType = CONTENT_TYPE_HEADER,
            ) {
              androidx.compose.material3.Text(
                text = stringResource(R.string.filter_albums),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
              )
            }

            items(
              items = sortedAlbums.distinctBy { it.id },
              key = { "album_${it.id}" },
              contentType = { "album" },
            ) { item ->
              AlbumGridItem(
                album = item,
                isActive = item.id == mediaMetadata?.album?.id,
                isPlaying = isPlaying,
                coroutineScope = coroutineScope,
                fillMaxWidth = true,
                modifier =
                  Modifier.fillMaxWidth()
                    .combinedClickable(
                      onClick = { navController.navigate("album/${item.id}") },
                      onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                          AlbumMenu(
                            originalAlbum = item,
                            navController = navController,
                            onDismiss = menuState::dismiss,
                          )
                        }
                      },
                    ),
              )
            }
          }
        }
    }
  }
}
