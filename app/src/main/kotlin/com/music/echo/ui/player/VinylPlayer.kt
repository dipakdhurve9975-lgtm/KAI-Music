package echo.music.iad1tya.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.KaiLiquidSegmentedControl
import echo.music.iad1tya.ui.component.backdrop.Backdrop
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import kotlinx.coroutines.isActive

enum class PlayerDisplayMode {
  VINYL,
  NORMAL,
  LYRICS,
}

/** Shared by both player orientations and retained while another display mode is visible. */
@Composable
fun rememberVinylRotation(playing: Boolean, visible: Boolean): State<Float> {
  val angle = rememberSaveable { mutableFloatStateOf(0f) }
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val motionEnabled = !rememberKaiReducedMotion()
  LaunchedEffect(playing, visible, motionEnabled, lifecycle) {
    if (playing && visible && motionEnabled)
      lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        var previous = withFrameNanos { it }
        while (isActive) {
          withFrameNanos { now ->
            angle.floatValue = advanceVinylAngle(angle.floatValue, now - previous)
            previous = now
          }
        }
      }
  }
  return angle
}

/** The record and arm are separate layers; only the disc and its label rotate. */
@Composable
fun VinylPlayerView(
  trackId: String?,
  artworkUrl: String?,
  rotation: State<Float>,
  engaged: Boolean,
  onArmSettled: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val surface = MaterialTheme.colorScheme.surfaceContainerHigh
  val edge = MaterialTheme.colorScheme.outlineVariant
  val accent = MaterialTheme.colorScheme.primary
  val labelColor = MaterialTheme.colorScheme.primaryContainer
  val labelText = MaterialTheme.colorScheme.onPrimaryContainer
  val label = stringResource(R.string.vinyl_label)
  val reduceMotion = rememberKaiReducedMotion()
  val context = LocalContext.current
  val density = LocalDensity.current
  val armSettled by rememberUpdatedState(onArmSettled)
  val armAngle = remember { Animatable(-24f) }
  LaunchedEffect(engaged, trackId, reduceMotion) {
    armSettled(false)
    if (reduceMotion) {
      armAngle.snapTo(if (engaged) 0f else -24f)
    } else if (engaged) {
      if (armAngle.value == 0f) armAngle.animateTo(-5f, tween(120))
      armAngle.animateTo(0f, tween(530))
    } else {
      armAngle.animateTo(-24f, tween(450))
    }
    armSettled(engaged)
  }

  BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    val side = minOf(maxWidth, maxHeight).coerceAtLeast(0.dp)
    Box(
      Modifier.size(side)
        .padding(12.dp)
        .shadow(14.dp, KaiShapes.card)
        .clip(KaiShapes.card)
        .background(Brush.linearGradient(listOf(surface, surface.copy(alpha = 0.82f))))
        .drawWithCache {
          onDrawBehind {
            // A fine, deterministic brushed finish, cached independently of rotation.
            for (i in 0..90) {
              val y = size.height * i / 90f
              drawLine(Color.White.copy(alpha = 0.025f), Offset(0f, y), Offset(size.width, y))
            }
            drawCircle(
              edge.copy(alpha = 0.4f),
              size.minDimension * 0.415f,
              Offset(size.width * 0.46f, size.height * 0.49f),
              style = Stroke(size.minDimension * 0.018f),
            )
          }
        }
    ) {
      BoxWithConstraints(Modifier.fillMaxSize()) {
        val discSize = maxWidth * 0.80f
        Box(
          Modifier.offset(x = maxWidth * 0.06f, y = maxHeight * 0.09f)
            .size(discSize)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .graphicsLayer { rotationZ = rotation.value }
            .drawWithCache {
              val vinyl =
                Brush.radialGradient(
                  listOf(Color(0xFF1B1B20), Color(0xFF08090B), Color(0xFF15161A))
                )
              val radii = FloatArray(104) { size.minDimension * (0.238f + it * 0.00238f) }
              val fineGroove = Stroke(0.24.dp.toPx())
              val fineLight = Stroke(0.18.dp.toPx())
              onDrawBehind {
                drawCircle(vinyl)
                radii.forEachIndexed { i, radius ->
                  drawCircle(
                    Color.Black.copy(alpha = if (i % 11 == 0) .62f else .32f),
                    radius,
                    style = fineGroove,
                  )
                  if (i % 3 == 0)
                    drawCircle(
                      Color.White.copy(alpha = if (i % 12 == 0) .065f else .027f),
                      radius + .45.dp.toPx(),
                      style = fineLight,
                    )
                }
                drawCircle(
                  Color(0xFF040507),
                  size.minDimension * .494f,
                  style = Stroke(2.dp.toPx()),
                )
                drawCircle(
                  Color.White.copy(alpha = .085f),
                  size.minDimension * .488f,
                  style = Stroke(.7.dp.toPx()),
                )
                drawCircle(
                  Color.Black.copy(alpha = .6f),
                  size.minDimension * .222f,
                  style = Stroke(1.6.dp.toPx()),
                )
                drawCircle(
                  Color.White.copy(alpha = .035f),
                  size.minDimension * .233f,
                  style = Stroke(.5.dp.toPx()),
                )
              }
            },
          contentAlignment = Alignment.Center,
        ) {
          Box(
            Modifier.fillMaxSize(0.43f).clip(CircleShape).background(labelColor),
            contentAlignment = Alignment.Center,
          ) {
            Text(label, color = labelText, style = MaterialTheme.typography.titleLarge)
            Crossfade(
              targetState = artworkUrl,
              animationSpec = tween(if (reduceMotion) 0 else 350),
              label = "vinylArtwork",
            ) { url ->
              AsyncImage(
                model =
                  remember(url, discSize, context) {
                    ImageRequest.Builder(context)
                      .data(url)
                      .size(with(density) { (discSize * .43f).roundToPx() }.coerceAtLeast(1))
                      .build()
                  },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
        }
        // Stationary reflected light and spindle share the disc's exact bounds and center.
        Box(
          Modifier.offset(x = maxWidth * .06f, y = maxHeight * .09f).size(discSize).drawWithCache {
            val discCenter = Offset(size.width / 2f, size.height / 2f)
            val reflection =
              Brush.sweepGradient(
                listOf(
                  Color.Transparent,
                  Color.White.copy(alpha = .055f),
                  Color.Transparent,
                  Color.Transparent,
                  Color.White.copy(alpha = .035f),
                  Color.Transparent,
                )
              )
            val ring =
              Path().apply {
                fillType = PathFillType.EvenOdd
                addOval(Rect(Offset.Zero, size))
                val inset = size.minDimension * .225f
                addOval(
                  Rect(
                    discCenter.x - inset,
                    discCenter.y - inset,
                    discCenter.x + inset,
                    discCenter.y + inset,
                  )
                )
              }
            val spindleRadius = size.minDimension * .0225f
            val spindle =
              Brush.radialGradient(
                listOf(Color(0xFFE4E0DB), Color(0xFF8B8C8E), Color(0xFF323438)),
                discCenter - Offset(spindleRadius * .2f, spindleRadius * .25f),
                spindleRadius * 1.5f,
              )
            onDrawBehind {
              drawPath(ring, reflection)
              drawCircle(
                Color.Black.copy(alpha = .38f),
                spindleRadius * 1.45f,
                center + Offset(0f, 1.dp.toPx()),
              )
              drawCircle(spindle, spindleRadius)
              drawCircle(Color(0xFF111215), spindleRadius * .36f)
              drawCircle(
                Color.White.copy(alpha = .32f),
                spindleRadius * .16f,
                center - Offset(spindleRadius * .25f, spindleRadius * .3f),
              )
            }
          }
        )
        // Tonearm paths and metal gradients are cached; only the draw transform reads its angle.
        Box(
          Modifier.fillMaxSize().drawWithCache {
            val w = size.width
            val h = size.height
            val pivot = Offset(w * .86f, h * .17f)
            val tip = Offset(w * .66f, h * .76f)
            val arm =
              Path().apply {
                moveTo(pivot.x, pivot.y)
                cubicTo(w * .81f, h * .34f, w * .78f, h * .57f, w * .72f, h * .67f)
                lineTo(tip.x, tip.y)
              }
            val base =
              Brush.radialGradient(listOf(Color(0xFF4A4C50), Color(0xFF101114)), pivot, w * .08f)
            val metal =
              Brush.linearGradient(
                listOf(Color(0xFF73777A), Color(0xFFC9CAC8), Color(0xFF6B6E72)),
                pivot,
                tip,
              )
            val armEdge = Stroke(w * .022f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val armMetal = Stroke(w * .011f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val armShadow = Stroke(w * .027f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            onDrawBehind {
              drawCircle(accent.copy(alpha = .80f), w * .007f, Offset(w * .1f, h * .9f))
              drawLine(
                edge.copy(alpha = .65f),
                Offset(w * .16f, h * .9f),
                Offset(w * .26f, h * .9f),
                2.dp.toPx(),
                StrokeCap.Round,
              )
              drawCircle(
                Color.Black.copy(alpha = .3f),
                w * .083f,
                pivot + Offset(2.dp.toPx(), 4.dp.toPx()),
              )
              drawCircle(base, w * .08f, pivot)
              rotate(armAngle.value, pivot) {
                translate(3.dp.toPx(), 4.dp.toPx()) {
                  drawPath(arm, Color.Black.copy(alpha = .30f), style = armShadow)
                }
                drawPath(arm, Color(0xFF313438), style = armEdge)
                drawPath(arm, metal, style = armMetal)
                rotate(18f, pivot) {
                  drawRoundRect(
                    Color(0xFF17191C),
                    Offset(pivot.x - w * .029f, pivot.y - h * .11f),
                    Size(w * .058f, h * .07f),
                    androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                  )
                  drawLine(
                    Color(0xFF707276),
                    Offset(pivot.x - w * .022f, pivot.y - h * .09f),
                    Offset(pivot.x + w * .022f, pivot.y - h * .09f),
                    1.dp.toPx(),
                  )
                }
                rotate(18f, tip) {
                  drawRoundRect(
                    Color(0xFF111316),
                    tip - Offset(w * .025f, h * .04f),
                    Size(w * .05f, h * .087f),
                    androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                  )
                  drawLine(Color(0xFF949A9F), tip, tip + Offset(0f, h * .035f), 1.5.dp.toPx())
                  drawLine(
                    Color.White.copy(alpha = .1f),
                    tip - Offset(w * .017f, h * .027f),
                    tip + Offset(w * .017f, -h * .027f),
                    1.dp.toPx(),
                  )
                }
              }
              drawCircle(Color(0xFF777B7F), w * .043f, pivot)
              drawCircle(Color(0xFF2B2D31), w * .035f, pivot)
              drawCircle(
                Color.White.copy(alpha = .06f),
                w * .027f,
                pivot - Offset(w * .004f, h * .004f),
              )
            }
          }
        )
      }
    }
  }
}

@Composable
fun PlayerModeSelector(
  selected: PlayerDisplayMode,
  onSelect: (PlayerDisplayMode) -> Unit,
  color: Color,
  modifier: Modifier = Modifier,
  backdrop: Backdrop? = null,
) {
  val modes = PlayerDisplayMode.entries
  val labels = modes.map { mode ->
    stringResource(
      when (mode) {
        PlayerDisplayMode.VINYL -> R.string.player_mode_vinyl
        PlayerDisplayMode.NORMAL -> R.string.player_mode_normal
        PlayerDisplayMode.LYRICS -> R.string.lyrics
      }
    )
  }
  KaiLiquidSegmentedControl(
    labels = labels,
    selectedIndex = modes.indexOf(selected),
    onSelectedIndexChange = { onSelect(modes[it]) },
    contentColor = color,
    modifier = modifier.padding(horizontal = KaiSpacing.page, vertical = KaiSpacing.xxs),
    backdrop = backdrop,
  )
}
