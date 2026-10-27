package echo.music.iad1tya.ui.screens.ambient

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.ui.player.KaiAlbumAtmosphere
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Reuse the same cached, low-cost atmospheric renderer as Now Playing. */
@Composable
fun AmbientGlowBackground(
  mediaMetadata: MediaMetadata?,
  modifier: Modifier = Modifier,
  active: Boolean = true,
) {
  val context = LocalContext.current
  val reducedMotion = rememberKaiReducedMotion()
  var colors by remember { mutableStateOf<List<Color>>(emptyList()) }
  val paletteCache = remember { linkedMapOf<String, List<Color>>() }
  LaunchedEffect(mediaMetadata?.id, mediaMetadata?.thumbnailUrl) {
    val metadata = mediaMetadata
    if (metadata?.thumbnailUrl == null) {
      colors = emptyList()
      return@LaunchedEffect
    }
    val cached = paletteCache[metadata.id]
    if (cached != null) {
      colors = cached
      return@LaunchedEffect
    }
    val extracted =
      try {
        withContext(Dispatchers.IO) {
          val request =
            ImageRequest.Builder(context)
              .data(metadata.thumbnailUrl)
              .size(100, 100)
              .allowHardware(false)
              .build()
          val image = context.imageLoader.execute(request).image?.toBitmap()
          image
            ?.let {
              val palette = Palette.from(it).maximumColorCount(8).resizeBitmapArea(10000).generate()
              val fallback = Color(0xFF39252F).toArgb()
              listOf(
                  Color(palette.getDarkMutedColor(fallback)),
                  Color(palette.getMutedColor(fallback)),
                  Color(palette.getDarkVibrantColor(fallback)),
                )
                .distinct()
            }
            .orEmpty()
        }
      } catch (cancelled: CancellationException) {
        throw cancelled
      } catch (_: Exception) {
        emptyList()
      }
    paletteCache[metadata.id] = extracted
    if (paletteCache.size > 12) paletteCache.remove(paletteCache.keys.first())
    colors = extracted
  }
  Crossfade(
    colors,
    modifier = modifier,
    animationSpec = tween(if (reducedMotion) 0 else 700),
    label = "ambientPalette",
  ) { palette ->
    KaiAlbumAtmosphere(palette, active = active, modifier = Modifier.fillMaxSize())
  }
}
