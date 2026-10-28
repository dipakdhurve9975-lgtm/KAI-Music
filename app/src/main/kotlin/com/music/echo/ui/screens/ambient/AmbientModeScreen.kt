/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.ambient

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import echo.music.iad1tya.LocalListenTogetherManager
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.ui.component.GlassComponent
import echo.music.iad1tya.ui.component.KaiLiquidSegmentedControl
import echo.music.iad1tya.ui.component.LocalGlassEffectConfig
import echo.music.iad1tya.ui.component.backdrop.backdrops.layerBackdrop
import echo.music.iad1tya.ui.component.backdrop.backdrops.rememberLayerBackdrop
import echo.music.iad1tya.ui.component.isGlassSupported
import echo.music.iad1tya.ui.player.InlineLyricsView
import echo.music.iad1tya.ui.player.VinylPlayerView
import echo.music.iad1tya.ui.player.rememberVinylRotation
import echo.music.iad1tya.ui.screens.settings.AmbientSettingsContent
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

/**
 * Both Ambient presentations render the current connection; this route never queues or starts
 * audio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientModeScreen(navController: NavController) {
  val context = LocalContext.current
  val connection = LocalPlayerConnection.current ?: return
  val metadata by connection.mediaMetadata.collectAsStateWithLifecycle()
  val playbackRequested by connection.isEffectivelyPlaying.collectAsStateWithLifecycle()
  val cast = remember(connection) { connection.service.castConnectionHandler }
  val casting by
    cast?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
  val castPlaying by
    cast?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
  val castBuffering by
    cast?.castIsBuffering?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
  val canNext by connection.canSkipNext.collectAsStateWithLifecycle()
  val canPrevious by connection.canSkipPrevious.collectAsStateWithLifecycle()
  val muted by connection.isMuted.collectAsStateWithLifecycle()
  val manager = LocalListenTogetherManager.current
  val guest by
    manager?.guestPlaybackRestricted?.collectAsStateWithLifecycle(initialValue = false)
      ?: remember { mutableStateOf(false) }
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  var resumed by
    remember(lifecycle) {
      mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
  var localPlaying by remember(connection) { mutableStateOf(connection.player.isPlaying) }
  DisposableEffect(connection, lifecycle) {
    val playerListener =
      object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
          localPlaying = player.isPlaying
        }
      }
    val lifecycleListener = LifecycleEventObserver { _, _ ->
      resumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
      localPlaying = connection.player.isPlaying
    }
    connection.player.addListener(playerListener)
    lifecycle.addObserver(lifecycleListener)
    onDispose {
      connection.player.removeListener(playerListener)
      lifecycle.removeObserver(lifecycleListener)
    }
  }
  val actuallyPlaying = if (casting) castPlaying && !castBuffering else localPlaying
  val reducedMotion = rememberKaiReducedMotion()
  var presentation by rememberEnumPreference(AmbientPresentationKey, AmbientPresentation.NORMAL)
  val vinyl = presentation == AmbientPresentation.VINYL
  val artScale by rememberPreference(AmbientArtScaleKey, .85f)
  val showArt by rememberPreference(AmbientShowArtKey, true)
  val fullScreenArt by rememberPreference(AmbientFullScreenArtKey, false)
  val showTitle by rememberPreference(AmbientShowTitleKey, false)
  val showArtist by rememberPreference(AmbientShowArtistKey, false)
  val showLyrics by rememberPreference(AmbientShowLyricsKey, true)
  val spacing by rememberPreference(AmbientSpacingKey, 32f)
  var armSettled by remember { mutableStateOf(false) }
  val rotation = rememberVinylRotation(actuallyPlaying && armSettled, vinyl && resumed)
  val audioManager =
    remember(context) { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
  var controlsVisible by remember { mutableStateOf(true) }
  var lastInteraction by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
  var touching by remember { mutableStateOf(false) }
  var settingsVisible by remember { mutableStateOf(false) }
  val accessibility =
    remember(context) {
      context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }
  var touchExploration by remember { mutableStateOf(accessibility.isTouchExplorationEnabled) }
  DisposableEffect(accessibility) {
    val listener = AccessibilityManager.TouchExplorationStateChangeListener {
      touchExploration = it
    }
    accessibility.addTouchExplorationStateChangeListener(listener)
    onDispose { accessibility.removeTouchExplorationStateChangeListener(listener) }
  }
  fun revealControls() {
    controlsVisible = true
    lastInteraction = SystemClock.elapsedRealtime()
  }
  LaunchedEffect(
    lastInteraction,
    touching,
    actuallyPlaying,
    settingsVisible,
    touchExploration,
    resumed,
  ) {
    if (!actuallyPlaying || settingsVisible || touchExploration || !resumed) controlsVisible = true
    else if (!touching) {
      delay(4000)
      controlsVisible = false
    }
  }

  val activity = LocalActivity.current
  val originalOrientation = rememberSaveable {
    activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }
  val originalKeepOn = rememberSaveable {
    ((activity?.window?.attributes?.flags ?: 0) and
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
  }
  val originalStatusVisible = rememberSaveable {
    activity?.window?.decorView?.let { view ->
      ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.statusBars())
        ?: ((view.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
    } ?: true
  }
  val originalNavigationVisible = rememberSaveable {
    activity?.window?.decorView?.let { view ->
      ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.navigationBars())
        ?: ((view.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0)
    } ?: true
  }
  val originalBarsBehavior = rememberSaveable {
    activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView).systemBarsBehavior }
      ?: WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
  }
  DisposableEffect(activity) {
    val window = activity?.window
    val controller = window?.let { WindowInsetsControllerCompat(it, it.decorView) }
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    controller?.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller?.hide(WindowInsetsCompat.Type.systemBars())
    onDispose {
      // A landscape request recreates this Activity. Keep the initial snapshot across that
      // recreation, and only restore orientation when actually leaving the Ambient route.
      if (activity?.isChangingConfigurations != true)
        activity?.requestedOrientation = originalOrientation
      if (originalKeepOn) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      controller?.systemBarsBehavior = originalBarsBehavior
      if (originalStatusVisible) controller?.show(WindowInsetsCompat.Type.statusBars())
      else controller?.hide(WindowInsetsCompat.Type.statusBars())
      if (originalNavigationVisible) controller?.show(WindowInsetsCompat.Type.navigationBars())
      else controller?.hide(WindowInsetsCompat.Type.navigationBars())
    }
  }
  BackHandler {
    if (settingsVisible) settingsVisible = false else navController.popBackStack()
  }

  val backdrop = rememberLayerBackdrop()
  val glassEnabled =
    isGlassSupported() && LocalGlassEffectConfig.current.isEnabledFor(GlassComponent.PLAYER)
  val showVisual = vinyl || showArt
  val infoVisible = showLyrics || vinyl || !showVisual || showTitle || showArtist
  val motionDuration = if (reducedMotion) 0 else 160
  Box(
    Modifier.fillMaxSize().background(Color(0xFF100D10)).pointerInput(Unit) {
      // Observe taps after child handlers; lyric seeking/scrolling keeps its own gestures.
      awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        touching = true
        var moved = false
        try {
          do {
            val event = awaitPointerEvent(PointerEventPass.Final)
            val pointer = event.changes.firstOrNull { it.id == down.id }
            if (
              pointer != null &&
                (pointer.position - down.position).getDistance() > viewConfiguration.touchSlop
            )
              moved = true
            val pressed = event.changes.any { it.pressed }
          } while (pressed)
          if (!moved) revealControls()
        } finally {
          touching = false
        }
      }
    }
  ) {
    Box(
      Modifier.fillMaxSize().then(if (glassEnabled) Modifier.layerBackdrop(backdrop) else Modifier)
    ) {
      AmbientGlowBackground(metadata, Modifier.fillMaxSize(), active = actuallyPlaying && resumed)
      if (!vinyl && fullScreenArt && showArt) {
        AsyncImage(
          metadata?.thumbnailUrl,
          null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          alpha = .16f,
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .38f)))
      }
    }
    Row(
      Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 24.dp, vertical = 64.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(spacing.coerceIn(0f, 100f).dp),
    ) {
      if (showVisual) {
        BoxWithConstraints(
          Modifier.weight(1f).fillMaxHeight(),
          contentAlignment = Alignment.Center,
        ) {
          val side = minOf(maxWidth, maxHeight) * if (vinyl) 1f else artScale.coerceIn(.3f, 1f)
          Box(
            Modifier.size(side)
              .pointerInput(guest, casting, canNext, canPrevious) {
                var drag = Offset.Zero
                var startVolume = 0f
                var maxVolume = 1
                detectDragGestures(
                  onDragStart = {
                    revealControls()
                    drag = Offset.Zero
                    maxVolume =
                      audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                    startVolume =
                      if (casting) cast?.castVolume?.value ?: 1f
                      else
                        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
                          maxVolume
                  },
                  onDragCancel = { drag = Offset.Zero },
                  onDrag = { change, amount ->
                    change.consume()
                    drag += amount
                    if (
                      !guest &&
                        abs(drag.y) > abs(drag.x) &&
                        abs(drag.y) > viewConfiguration.touchSlop
                    ) {
                      val volume =
                        (startVolume - drag.y / size.height.coerceAtLeast(1)).coerceIn(0f, 1f)
                      if (casting) cast?.setVolume(volume)
                      else
                        audioManager.setStreamVolume(
                          AudioManager.STREAM_MUSIC,
                          (volume * maxVolume).roundToInt(),
                          AudioManager.FLAG_SHOW_UI,
                        )
                    }
                  },
                  onDragEnd = {
                    if (!guest && abs(drag.x) > size.width * .20f && abs(drag.x) > abs(drag.y)) {
                      if (drag.x > 0 && canPrevious) connection.seekToPrevious()
                      else if (drag.x < 0 && canNext) connection.seekToNext()
                    }
                    drag = Offset.Zero
                    revealControls()
                  },
                )
              }
              .pointerInput(guest) {
                detectTapGestures(
                  onDoubleTap = {
                    if (guest) connection.toggleMute() else connection.togglePlayPause()
                    revealControls()
                  }
                )
              }
          ) {
            if (vinyl) {
              VinylPlayerView(
                metadata?.id,
                metadata?.thumbnailUrl,
                rotation,
                engaged = resumed && metadata != null,
                onArmSettled = { armSettled = it },
                modifier = Modifier.fillMaxSize(),
              )
            } else {
              Box(Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))) {
                AsyncImage(
                  metadata?.thumbnailUrl,
                  stringResource(R.string.kai_ambient_art_description),
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop,
                  placeholder = painterResource(R.drawable.kai_mark),
                  error = painterResource(R.drawable.kai_mark),
                )
                AmbientCanvasBackground(
                  Modifier.fillMaxSize(),
                  active = actuallyPlaying,
                  enabled = resumed && !reducedMotion,
                )
              }
            }
          }
        }
      }
      if (infoVisible) {
        Column(
          Modifier.weight(1f).fillMaxHeight(),
          verticalArrangement = if (showLyrics) Arrangement.Top else Arrangement.Center,
          horizontalAlignment = if (showVisual) Alignment.Start else Alignment.CenterHorizontally,
        ) {
          if (metadata == null) {
            Text(
              stringResource(R.string.kai_ambient_empty),
              color = Color.White,
              style = MaterialTheme.typography.headlineSmall,
            )
          } else {
            if (showTitle || vinyl)
              Text(
                metadata?.title.orEmpty(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
              )
            if (showArtist || vinyl)
              Text(
                metadata?.artists?.joinToString { it.name }.orEmpty(),
                color = Color.White.copy(alpha = .72f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
              )
            if (showLyrics) {
              Spacer(Modifier.height(if (showTitle || showArtist || vinyl) 16.dp else 0.dp))
              InlineLyricsView(
                mediaMetadata = metadata,
                showLyrics = resumed,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentColor = Color.White,
                positionProvider = { connection.player.currentPosition },
              )
            }
          }
        }
      }
    }
    AnimatedVisibility(
      visible = controlsVisible,
      enter = fadeIn(tween(motionDuration)),
      exit = fadeOut(tween(motionDuration)),
      modifier = Modifier.align(Alignment.TopCenter),
    ) {
      Row(
        Modifier.fillMaxWidth().safeDrawingPadding().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(48.dp)) {
          Icon(
            painterResource(R.drawable.arrow_back),
            stringResource(R.string.kai_ambient_exit),
            tint = Color.White,
          )
        }
        KaiLiquidSegmentedControl(
          labels =
            listOf(
              stringResource(R.string.kai_ambient_normal),
              stringResource(R.string.kai_ambient_vinyl),
            ),
          selectedIndex = presentation.ordinal,
          onSelectedIndexChange = {
            presentation = AmbientPresentation.entries[it]
            revealControls()
          },
          contentColor = Color.White,
          modifier = Modifier.width(240.dp),
          backdrop = if (glassEnabled) backdrop else null,
        )
        IconButton(
          onClick = {
            settingsVisible = true
            revealControls()
          },
          modifier = Modifier.size(48.dp),
        ) {
          Icon(
            painterResource(R.drawable.settings),
            stringResource(R.string.kai_ambient_settings),
            tint = Color.White,
          )
        }
      }
    }
    AnimatedVisibility(
      visible = controlsVisible,
      enter = fadeIn(tween(motionDuration)),
      exit = fadeOut(tween(motionDuration)),
      modifier = Modifier.align(Alignment.BottomCenter),
    ) {
      Row(
        Modifier.fillMaxWidth()
          .background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .30f)))
          )
          .safeDrawingPadding()
          .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        IconButton(
          onClick = {
            connection.seekToPrevious()
            revealControls()
          },
          enabled = canPrevious && !guest,
          modifier = Modifier.size(48.dp),
        ) {
          Icon(
            painterResource(R.drawable.skip_previous),
            stringResource(R.string.previous),
            tint = Color.White.copy(alpha = if (canPrevious && !guest) 1f else .35f),
          )
        }
        IconButton(
          onClick = {
            if (guest) connection.toggleMute() else connection.togglePlayPause()
            revealControls()
          },
          modifier = Modifier.size(56.dp),
        ) {
          Icon(
            painterResource(
              if (guest) {
                if (muted) R.drawable.volume_off else R.drawable.volume_up
              } else if (playbackRequested) R.drawable.pause else R.drawable.play
            ),
            stringResource(
              if (guest) {
                if (muted) R.string.unmute else R.string.mute
              } else if (playbackRequested) R.string.pause else R.string.play
            ),
            tint = Color.White,
            modifier = Modifier.size(30.dp),
          )
        }
        IconButton(
          onClick = {
            connection.seekToNext()
            revealControls()
          },
          enabled = canNext && !guest,
          modifier = Modifier.size(48.dp),
        ) {
          Icon(
            painterResource(R.drawable.skip_next),
            stringResource(R.string.next),
            tint = Color.White.copy(alpha = if (canNext && !guest) 1f else .35f),
          )
        }
      }
    }
    if (!controlsVisible) {
      IconButton(
        onClick = ::revealControls,
        modifier =
          Modifier.align(Alignment.BottomEnd).safeDrawingPadding().padding(8.dp).size(48.dp),
      ) {
        Icon(
          painterResource(R.drawable.more_horiz),
          stringResource(R.string.kai_ambient_controls),
          tint = Color.White.copy(alpha = .45f),
        )
      }
    }
    if (settingsVisible) {
      Dialog(
        onDismissRequest = {
          settingsVisible = false
          revealControls()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
      ) {
        BoxWithConstraints(
          Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = .32f))
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
            ) {
              settingsVisible = false
              revealControls()
            }
            .padding(16.dp),
          contentAlignment = Alignment.CenterEnd,
        ) {
          Surface(
            Modifier.width(minOf(maxWidth, 420.dp)).fillMaxHeight().clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
            ) {},
            shape = RoundedCornerShape(24.dp),
          ) {
            AmbientSettingsContent(Modifier.padding(8.dp))
          }
        }
      }
    }
  }
}
