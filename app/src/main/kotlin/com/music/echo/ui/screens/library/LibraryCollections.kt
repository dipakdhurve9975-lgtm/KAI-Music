/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.AutoPlaylistButton
import echo.music.iad1tya.ui.theme.KaiShapes

private data class LibraryCollection(val title: String, val icon: Int, val route: String)

/** One compact group shared by mixed and playlist views; all existing collection routes remain. */
@Composable
internal fun LibraryCollections(
  navController: NavController,
  topSize: String,
  showLiked: Boolean,
  showDownloaded: Boolean,
  showExported: Boolean,
  showCached: Boolean,
  showTop: Boolean,
  showBottom: Boolean,
  showTasteMix: Boolean = false,
) {
  val collections = buildList {
    if (showLiked)
      add(
        LibraryCollection(
          stringResource(R.string.liked),
          R.drawable.favorite,
          "auto_playlist/liked",
        )
      )
    if (showDownloaded)
      add(
        LibraryCollection(
          stringResource(R.string.offline),
          R.drawable.offline,
          "auto_playlist/downloaded",
        )
      )
    if (showExported)
      add(
        LibraryCollection(
          stringResource(R.string.action_exported),
          R.drawable.download,
          "auto_playlist/exported",
        )
      )
    if (showCached)
      add(
        LibraryCollection(
          stringResource(R.string.cached_playlist),
          R.drawable.cached,
          "cache_playlist/cached",
        )
      )
    if (showTop)
      add(
        LibraryCollection(
          stringResource(R.string.my_top) + " $topSize",
          R.drawable.trending_up,
          "top_playlist/$topSize",
        )
      )
    if (showBottom)
      add(
        LibraryCollection(
          stringResource(R.string.my_bottom) + " 50",
          R.drawable.trending_down,
          "bottom_playlist/50",
        )
      )
    if (showTasteMix)
      add(
        LibraryCollection(
          stringResource(R.string.kai_taste_mix),
          R.drawable.playlist_play,
          "generate",
        )
      )
    else
      add(
        LibraryCollection(
          stringResource(R.string.filter_local),
          R.drawable.snippet_folder,
          "local_songs",
        )
      )
  }
  Surface(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
    shape = KaiShapes.card,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      collections.chunked(2).forEach { pair ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          pair.forEach { collection ->
            AutoPlaylistButton(
              title = collection.title,
              icon = collection.icon,
              iconTint = MaterialTheme.colorScheme.onSurface,
              onClick = { navController.navigate(collection.route) },
              modifier = Modifier.weight(1f),
            )
          }
          if (pair.size == 1) Spacer(Modifier.weight(1f))
        }
      }
    }
  }
}
