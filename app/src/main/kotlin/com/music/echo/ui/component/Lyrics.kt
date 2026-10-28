/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Layout
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalListenTogetherManager
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AiProviderKey
import echo.music.iad1tya.constants.AutoTranslateKey
import echo.music.iad1tya.constants.DarkModeKey
import echo.music.iad1tya.constants.DeeplApiKey
import echo.music.iad1tya.constants.DeeplFormalityKey
import echo.music.iad1tya.constants.LyricsAnimationStyle
import echo.music.iad1tya.constants.LyricsAnimationStyleKey
import echo.music.iad1tya.constants.LyricsClickKey
import echo.music.iad1tya.constants.LyricsGlowEffectKey
import echo.music.iad1tya.constants.LyricsLineSpacingKey
import echo.music.iad1tya.constants.LyricsPresentationStyle
import echo.music.iad1tya.constants.LyricsRomanizeAsMainKey
import echo.music.iad1tya.constants.LyricsRomanizeBelarusianKey
import echo.music.iad1tya.constants.LyricsRomanizeBulgarianKey
import echo.music.iad1tya.constants.LyricsRomanizeChineseKey
import echo.music.iad1tya.constants.LyricsRomanizeCyrillicByLineKey
import echo.music.iad1tya.constants.LyricsRomanizeHindiKey
import echo.music.iad1tya.constants.LyricsRomanizeJapaneseKey
import echo.music.iad1tya.constants.LyricsRomanizeKoreanKey
import echo.music.iad1tya.constants.LyricsRomanizeKyrgyzKey
import echo.music.iad1tya.constants.LyricsRomanizeMacedonianKey
import echo.music.iad1tya.constants.LyricsRomanizePunjabiKey
import echo.music.iad1tya.constants.LyricsRomanizeRussianKey
import echo.music.iad1tya.constants.LyricsRomanizeSerbianKey
import echo.music.iad1tya.constants.LyricsRomanizeUkrainianKey
import echo.music.iad1tya.constants.LyricsScrollKey
import echo.music.iad1tya.constants.LyricsStandardBlurKey
import echo.music.iad1tya.constants.LyricsTextPositionKey
import echo.music.iad1tya.constants.LyricsTextSizeKey
import echo.music.iad1tya.constants.OpenRouterApiKey
import echo.music.iad1tya.constants.OpenRouterBaseUrlKey
import echo.music.iad1tya.constants.OpenRouterModelKey
import echo.music.iad1tya.constants.PlayerBackgroundStyle
import echo.music.iad1tya.constants.PlayerBackgroundStyleKey
import echo.music.iad1tya.constants.TranslateLanguageKey
import echo.music.iad1tya.constants.TranslateModeKey
import echo.music.iad1tya.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import echo.music.iad1tya.lyrics.LyricsEntry
import echo.music.iad1tya.lyrics.LyricsTranslationHelper
import echo.music.iad1tya.lyrics.LyricsUtils.findCurrentLineIndex
import echo.music.iad1tya.lyrics.LyricsUtils.isBelarusian
import echo.music.iad1tya.lyrics.LyricsUtils.isBulgarian
import echo.music.iad1tya.lyrics.LyricsUtils.isChinese
import echo.music.iad1tya.lyrics.LyricsUtils.isHindi
import echo.music.iad1tya.lyrics.LyricsUtils.isJapanese
import echo.music.iad1tya.lyrics.LyricsUtils.isKorean
import echo.music.iad1tya.lyrics.LyricsUtils.isKyrgyz
import echo.music.iad1tya.lyrics.LyricsUtils.isMacedonian
import echo.music.iad1tya.lyrics.LyricsUtils.isPunjabi
import echo.music.iad1tya.lyrics.LyricsUtils.isRussian
import echo.music.iad1tya.lyrics.LyricsUtils.isSerbian
import echo.music.iad1tya.lyrics.LyricsUtils.isUkrainian
import echo.music.iad1tya.lyrics.LyricsUtils.parseLyrics
import echo.music.iad1tya.lyrics.LyricsUtils.romanizeChinese
import echo.music.iad1tya.lyrics.LyricsUtils.romanizeCyrillic
import echo.music.iad1tya.lyrics.LyricsUtils.romanizeHindi
import echo.music.iad1tya.lyrics.LyricsUtils.romanizeJapanese
import echo.music.iad1tya.lyrics.LyricsUtils.romanizeKorean
import echo.music.iad1tya.lyrics.LyricsUtils.romanizePunjabi
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import echo.music.iad1tya.ui.screens.settings.DarkMode
import echo.music.iad1tya.ui.screens.settings.LyricsPosition
import echo.music.iad1tya.ui.theme.KaiMotion
import echo.music.iad1tya.ui.theme.rememberKaiReducedMotion
import echo.music.iad1tya.ui.utils.fadingEdge
import echo.music.iad1tya.utils.ComposeToImage
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope", "StringFormatInvalid")
@Composable
fun Lyrics(
  sliderPositionProvider: () -> Long?,
  modifier: Modifier = Modifier,
  showLyrics: Boolean,
  compact: Boolean = false,
  contentColor: Color = Color.White,
) {
  val playerConnection = LocalPlayerConnection.current ?: return
  val database = LocalDatabase.current
  val menuState = LocalMenuState.current
  val density = LocalDensity.current
  val hapticFeedback = LocalHapticFeedback.current
  val context = LocalContext.current
  val configuration = LocalWindowInfo.current
  val listenTogetherManager = LocalListenTogetherManager.current
  val isGuest by
    listenTogetherManager?.guestPlaybackRestricted?.collectAsState(initial = false)
      ?: remember { mutableStateOf(false) }

  val lyricsTextPosition by rememberEnumPreference(LyricsTextPositionKey, LyricsPosition.LEFT)
  val changeLyrics by rememberPreference(LyricsClickKey, true)
  val scrollLyrics by rememberPreference(LyricsScrollKey, true)
  val romanizeJapaneseLyrics by rememberPreference(LyricsRomanizeJapaneseKey, true)
  val romanizeKoreanLyrics by rememberPreference(LyricsRomanizeKoreanKey, true)
  val romanizeRussianLyrics by rememberPreference(LyricsRomanizeRussianKey, true)
  val romanizeUkrainianLyrics by rememberPreference(LyricsRomanizeUkrainianKey, true)
  val romanizeSerbianLyrics by rememberPreference(LyricsRomanizeSerbianKey, true)
  val romanizeBulgarianLyrics by rememberPreference(LyricsRomanizeBulgarianKey, true)
  val romanizeBelarusianLyrics by rememberPreference(LyricsRomanizeBelarusianKey, true)
  val romanizeKyrgyzLyrics by rememberPreference(LyricsRomanizeKyrgyzKey, true)
  val romanizeMacedonianLyrics by rememberPreference(LyricsRomanizeMacedonianKey, true)
  val romanizeCyrillicByLine by rememberPreference(LyricsRomanizeCyrillicByLineKey, false)
  val romanizeAsMain by rememberPreference(LyricsRomanizeAsMainKey, false)
  val romanizeChineseLyrics by rememberPreference(LyricsRomanizeChineseKey, true)
  val romanizeHindiLyrics by rememberPreference(LyricsRomanizeHindiKey, true)
  val romanizePunjabiLyrics by rememberPreference(LyricsRomanizePunjabiKey, true)
  val lyricsTypeface = rememberLyricsTypeface()
  val lyricsGlowEffect by rememberPreference(LyricsGlowEffectKey, false)
  val preferredLyricsAnimationStyle by
    rememberEnumPreference(LyricsAnimationStyleKey, LyricsAnimationStyle.FADE)
  val reduceMotion = rememberKaiReducedMotion()
  val lyricsAnimationStyle =
    if (reduceMotion) LyricsAnimationStyle.NONE else preferredLyricsAnimationStyle
  val lyricsTextSize by rememberPreference(LyricsTextSizeKey, 28f)
  val lyricsLineSpacing by rememberPreference(LyricsLineSpacingKey, 1.3f)
  val lyricsStandardBlur by rememberPreference(LyricsStandardBlurKey, false)

  val openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
  val deeplApiKey by rememberPreference(DeeplApiKey, "")
  val aiProvider by rememberPreference(AiProviderKey, "OpenRouter")
  val openRouterBaseUrl by
    rememberPreference(OpenRouterBaseUrlKey, "https://openrouter.ai/api/v1/chat/completions")
  val openRouterModel by rememberPreference(OpenRouterModelKey, "openrouter/free")
  val translateLanguage by rememberPreference(TranslateLanguageKey, "en")
  val translateMode by rememberPreference(TranslateModeKey, "Literal")
  val autoTranslate by rememberPreference(AutoTranslateKey, false)
  val deeplFormality by rememberPreference(DeeplFormalityKey, "default")

  val scope = rememberCoroutineScope()

  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
  val lyricsSnapshot by playerConnection.currentLyrics.collectAsState(initial = null)
  val songSnapshot by playerConnection.currentSong.collectAsState(initial = null)
  // flatMapLatest DB flows can retain the preceding entity until the next query emits.
  // Never apply another track's text, romanization preference or offset to the new timeline.
  val lyricsEntity = lyricsSnapshot?.takeIf { it.id == mediaMetadata?.id }
  val currentSong = songSnapshot?.takeIf { it.id == mediaMetadata?.id }
  val lyrics = remember(lyricsEntity) { lyricsEntity?.lyrics?.trim() }
  val presentation = rememberLyricsPresentation(mediaMetadata?.id)

  val playerBackground by
    rememberEnumPreference(
      key = PlayerBackgroundStyleKey,
      defaultValue = PlayerBackgroundStyle.GRADIENT,
    )

  val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
  val isSystemInDarkTheme = isSystemInDarkTheme()
  val useDarkTheme =
    remember(darkTheme, isSystemInDarkTheme) {
      if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }

  val currentSongRomanize = currentSong?.song?.romanizeLyrics ?: true
  val parsedLines = remember(lyrics) { lyrics?.let(::parseLyrics).orEmpty() }
  val isSynced =
    remember(parsedLines) {
      echo.music.iad1tya.lyrics.LyricsUtils.hasUsableLyricTiming(parsedLines)
    }
  LaunchedEffect(mediaMetadata?.id, lyricsEntity?.lyrics, showLyrics) {
    val metadata = mediaMetadata ?: return@LaunchedEffect
    val original = lyricsEntity ?: return@LaunchedEffect
    if (
      !showLyrics ||
        isSynced ||
        original.lyrics.isBlank() ||
        original.lyrics == LYRICS_NOT_FOUND ||
        original.provider == "Unknown"
    )
      return@LaunchedEffect
    withContext(Dispatchers.IO) {
      try {
        val helper =
          dagger.hilt.android.EntryPointAccessors.fromApplication(
              context.applicationContext,
              echo.music.iad1tya.di.LyricsHelperEntryPoint::class.java,
            )
            .lyricsHelper()
        val upgrade = helper.getTimedUpgrade(metadata) ?: return@withContext
        val latest = database.lyrics(metadata.id).first()
        if (latest?.lyrics == original.lyrics)
          database.query {
            upsert(
              original.copy(
                lyrics = upgrade.lyrics!!,
                provider = upgrade.providerName,
                translatedLyrics = "",
                translationLanguage = "",
                translationMode = "",
              )
            )
          }
      } catch (cancelled: kotlinx.coroutines.CancellationException) {
        throw cancelled
      } catch (_: Exception) {
        /* Keep the existing readable lyrics on any provider failure. */
      }
    }
  }
  val lines =
    remember(lyrics, parsedLines, isSynced) {
      when {
        lyrics == null || lyrics == LYRICS_NOT_FOUND -> emptyList()
        parsedLines.isNotEmpty() ->
          (if (isSynced) listOf(LyricsEntry.HEAD_LYRICS_ENTRY) else emptyList()) +
            parsedLines.map {
              LyricsEntry(
                it.time,
                it.text,
                it.words,
                agent = it.agent,
                isBackground = it.isBackground,
              )
            }
        else -> lyrics.lines().map { LyricsEntry(0L, it) }
      }
    }
  LaunchedEffect(
    lines,
    currentSongRomanize,
    romanizeJapaneseLyrics,
    romanizeKoreanLyrics,
    romanizeRussianLyrics,
    romanizeUkrainianLyrics,
    romanizeSerbianLyrics,
    romanizeBulgarianLyrics,
    romanizeBelarusianLyrics,
    romanizeKyrgyzLyrics,
    romanizeMacedonianLyrics,
    romanizeCyrillicByLine,
    romanizeChineseLyrics,
    romanizeHindiLyrics,
    romanizePunjabiLyrics,
  ) {
    lines.forEach { it.romanizedTextFlow.value = null }
    if (currentSongRomanize) {
      val wholeText = lyrics.orEmpty()
      lines.forEach { entry ->
        launch(Dispatchers.Default) {
          val text = entry.text
          entry.romanizedTextFlow.value =
            when {
              romanizeJapaneseLyrics && isJapanese(text) && !isChinese(text) ->
                romanizeJapanese(text)
              romanizeKoreanLyrics && isKorean(text) -> romanizeKorean(text)
              romanizeRussianLyrics && isRussian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeUkrainianLyrics &&
                isUkrainian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeSerbianLyrics && isSerbian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeBulgarianLyrics &&
                isBulgarian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeBelarusianLyrics &&
                isBelarusian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeKyrgyzLyrics && isKyrgyz(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeMacedonianLyrics &&
                isMacedonian(if (romanizeCyrillicByLine) text else wholeText) ->
                romanizeCyrillic(text)
              romanizeChineseLyrics && isChinese(text) -> romanizeChinese(text)
              romanizeHindiLyrics && isHindi(text) -> romanizeHindi(text)
              romanizePunjabiLyrics && isPunjabi(text) -> romanizePunjabi(text)
              else -> null
            }
        }
      }
    }
  }
  val translationStatus by LyricsTranslationHelper.status.collectAsState()
  val hasActiveTranslations by LyricsTranslationHelper.hasActiveTranslations.collectAsState()
  var showTranslationBanner by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    LyricsTranslationHelper.setCompositionActive(true)
    onDispose {
      LyricsTranslationHelper.setCompositionActive(false)
      LyricsTranslationHelper.cancelTranslation()
      LyricsTranslationHelper.resetStatus()
    }
  }

  LaunchedEffect(mediaMetadata?.id) {
    LyricsTranslationHelper.cancelTranslation()
    LyricsTranslationHelper.resetStatus()
    showTranslationBanner = false
  }

  LaunchedEffect(translationStatus) {
    if (translationStatus !is LyricsTranslationHelper.TranslationStatus.Idle) {
      showTranslationBanner = true
      kotlinx.coroutines.delay(3000)
      showTranslationBanner = false
      if (
        translationStatus is LyricsTranslationHelper.TranslationStatus.Error ||
          translationStatus is LyricsTranslationHelper.TranslationStatus.Success
      ) {
        LyricsTranslationHelper.resetStatus()
      }
    } else {
      showTranslationBanner = false
    }
  }

  LaunchedEffect(lines, lyricsEntity, translateLanguage, translateMode, autoTranslate) {
    if (lines.isNotEmpty() && lyricsEntity != null) {
      LyricsTranslationHelper.loadTranslationsFromDatabase(
        lyrics = lines,
        lyricsEntity = lyricsEntity,
        targetLanguage = translateLanguage,
        mode = translateMode,
      )

      kotlinx.coroutines.delay(100)

      val effectiveApiKey = if (aiProvider == "DeepL") deeplApiKey else openRouterApiKey
      if (
        autoTranslate &&
          effectiveApiKey.isNotBlank() &&
          !LyricsTranslationHelper.hasTranslations(lyricsEntity) &&
          LyricsTranslationHelper.status.value !is
            LyricsTranslationHelper.TranslationStatus.Translating &&
          !LyricsTranslationHelper.hasActiveTranslations.value
      ) {
        LyricsTranslationHelper.triggerManualTranslation()
      }
    }
  }

  LaunchedEffect(
    showLyrics,
    lines,
    mediaMetadata?.id,
    autoTranslate,
    aiProvider,
    translateLanguage,
    translateMode,
  ) {
    LyricsTranslationHelper.manualTrigger.collect {
      val effectiveApiKey = if (aiProvider == "DeepL") deeplApiKey else openRouterApiKey
      if (showLyrics && lines.isNotEmpty() && effectiveApiKey.isNotBlank()) {
        LyricsTranslationHelper.translateLyrics(
          lyrics = lines,
          targetLanguage = translateLanguage,
          apiKey = openRouterApiKey,
          baseUrl = openRouterBaseUrl,
          model = openRouterModel,
          mode = translateMode,
          scope = scope,
          context = context,
          provider = aiProvider,
          deeplApiKey = deeplApiKey,
          deeplFormality = deeplFormality,
          useStreaming = true,
          songId = currentSong?.id ?: "",
          database = database,
        )
      } else if (effectiveApiKey.isBlank()) {
        Toast.makeText(context, context.getString(R.string.ai_api_key_required), Toast.LENGTH_SHORT)
          .show()
      }
    }
  }

  LaunchedEffect(lines) {
    LyricsTranslationHelper.clearTranslationsTrigger.collect {
      lines.forEach { it.translatedTextFlow.value = null }
    }
  }

  val expressiveAccent = contentColor
  val textColor = contentColor

  var currentLineIndex by remember(mediaMetadata?.id, lyrics) { mutableIntStateOf(-1) }
  val playbackClock =
    rememberLyricsPlaybackClock(
      playerConnection,
      mediaMetadata?.id,
      showLyrics && isSynced,
      sliderPositionProvider,
    )

  var deferredCurrentLineIndex by rememberSaveable { mutableIntStateOf(0) }

  var lastPreviewTime by rememberSaveable { mutableLongStateOf(0L) }
  val isSeeking by playbackClock.seeking

  var showProgressDialog by remember { mutableStateOf(false) }
  var showShareDialog by remember { mutableStateOf(false) }
  var shareDialogData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

  var showColorPickerDialog by remember { mutableStateOf(false) }
  var previewBackgroundColor by remember { mutableStateOf(Color(0xFF242424)) }
  var previewTextColor by remember { mutableStateOf(Color.White) }
  var previewSecondaryTextColor by remember { mutableStateOf(Color.White.copy(alpha = 0.7f)) }

  var isSelectionModeActive by rememberSaveable { mutableStateOf(false) }
  val selectedIndices = remember { mutableStateListOf<Int>() }
  var showMaxSelectionToast by remember { mutableStateOf(false) }

  val isLyricsProviderShown =
    !compact &&
      lyricsEntity?.provider != null &&
      lyricsEntity?.provider != "Unknown" &&
      !isSelectionModeActive

  val lazyListState = rememberLazyListState()
  var isAutoScrollEnabled by rememberSaveable { mutableStateOf(true) }
  var lastManualScrollTime by remember { mutableStateOf(0L) }

  LaunchedEffect(
    isAutoScrollEnabled,
    isSynced,
    isSelectionModeActive,
    showLyrics,
    lazyListState.isScrollInProgress,
    lastManualScrollTime,
  ) {
    if (
      !isAutoScrollEnabled &&
        isSynced &&
        !isSelectionModeActive &&
        showLyrics &&
        !lazyListState.isScrollInProgress
    ) {
      delay(4000)
      lastPreviewTime = 0L
      isAutoScrollEnabled = true
    }
  }

  BackHandler(enabled = isSelectionModeActive) {
    isSelectionModeActive = false
    selectedIndices.clear()
  }

  val maxSelectionLimit = 5

  LaunchedEffect(showMaxSelectionToast) {
    if (showMaxSelectionToast) {
      Toast.makeText(
          context,
          context.getString(R.string.max_selection_limit, maxSelectionLimit),
          Toast.LENGTH_SHORT,
        )
        .show()
      showMaxSelectionToast = false
    }
  }

  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(showLyrics) {
    val activity = context as? Activity
    val alreadyKeptOn =
      activity?.window?.attributes?.flags?.and(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
    if (showLyrics) activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    onDispose {
      if (showLyrics && !alreadyKeptOn)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
  }

  var resumeEpoch by remember { mutableIntStateOf(0) }
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) resumeEpoch++
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  LaunchedEffect(lines) {
    isSelectionModeActive = false
    selectedIndices.clear()
    isAutoScrollEnabled = true
    if (!isSynced) lazyListState.scrollToItem(0)
  }

  val lyricsOffsetMs = currentSong?.song?.lyricsOffset?.toLong() ?: 0L
  LaunchedEffect(lines, isSynced, lyricsOffsetMs, showLyrics) {
    if (!isSynced || !showLyrics) {
      currentLineIndex = -1
    } else {
      snapshotFlow { playbackClock.position.value + lyricsOffsetMs }
        .collect { position ->
          currentLineIndex = findCurrentLineIndex(lines, position)
        }
    }
  }

  LaunchedEffect(isSeeking, lastPreviewTime) {
    if (isSeeking) {
      lastPreviewTime = 0L
    } else if (lastPreviewTime != 0L) {
      delay(LyricsPreviewTime)
      lastPreviewTime = 0L
    }
  }

  suspend fun performSmoothPageScroll(targetIndex: Int, duration: Int = 1500) {
    val lookUpIndex = if (isLyricsProviderShown) targetIndex + 1 else targetIndex
    if (reduceMotion) {
      lazyListState.scrollToItem(
        lookUpIndex.coerceIn(0, (lazyListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
      )
      return
    }
    val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == lookUpIndex }
    if (itemInfo != null) {
      val viewportHeight =
        lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
      val anchor =
        if (compact) lazyListState.layoutInfo.viewportStartOffset
        else lazyListState.layoutInfo.viewportStartOffset + (viewportHeight / 2)
      val itemAnchor = if (compact) itemInfo.offset else itemInfo.offset + itemInfo.size / 2
      val offset = itemAnchor - anchor
      if (kotlin.math.abs(offset) > 10) {
        lazyListState.animateScrollBy(
          value = offset.toFloat(),
          animationSpec = tween(duration.coerceIn(200, 350), easing = KaiMotion.easing),
        )
      }
    } else {
      lazyListState.scrollToItem(lookUpIndex)
    }
  }
  LaunchedEffect(
    lines,
    currentLineIndex,
    resumeEpoch,
    isSeeking,
    isAutoScrollEnabled,
    isSelectionModeActive,
    showLyrics,
    presentation.ready,
    presentation.style,
  ) {
    if (
      !showLyrics ||
        !presentation.ready ||
        !isSynced ||
        !isAutoScrollEnabled ||
        isSelectionModeActive
    )
      return@LaunchedEffect
    deferredCurrentLineIndex = currentLineIndex
    if (currentLineIndex >= 0 && (scrollLyrics || isSeeking)) {
      performSmoothPageScroll(currentLineIndex, if (isSeeking) 200 else 300)
    }
  }

  LaunchedEffect(mediaMetadata?.id, lines, currentLineIndex, lyricsOffsetMs, isAutoScrollEnabled) {
    if (echo.music.iad1tya.BuildConfig.DEBUG && showLyrics)
      android.util.Log.d(
        "KaiLyricsSync",
        "track=${mediaMetadata?.id} position=${playbackClock.position.value} index=$currentLineIndex " +
          "current=${lines.getOrNull(currentLineIndex)?.time} next=${lines.getOrNull(currentLineIndex + 1)?.time} " +
          "offset=$lyricsOffsetMs scroll=${if (isAutoScrollEnabled) currentLineIndex + if (isLyricsProviderShown) 1 else 0 else -1} synced=$isSynced",
      )
  }

  BoxWithConstraints(
    contentAlignment = Alignment.TopCenter,
    modifier = modifier.fillMaxSize().clipToBounds().padding(bottom = 12.dp),
  ) {
    AnimatedVisibility(
      visible =
        !compact &&
          showTranslationBanner &&
          translationStatus !is LyricsTranslationHelper.TranslationStatus.Idle,
      enter = fadeIn() + slideInVertically { -it },
      exit = fadeOut() + slideOutVertically { -it },
      modifier = Modifier.fillMaxWidth().zIndex(1f).padding(top = 56.dp),
    ) {
      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        when (val status = translationStatus) {
          is LyricsTranslationHelper.TranslationStatus.Translating -> {
            Card(
              colors =
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
              shape = RoundedCornerShape(16.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                KaiSpiralLoading(
                  modifier = Modifier.size(16.dp),
                  strokeWidth = 2.dp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                  text =
                    if (status.logs.isNotEmpty()) status.logs.last()
                    else stringResource(R.string.ai_translating_lyrics),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
            }
          }
          is LyricsTranslationHelper.TranslationStatus.Error -> {
            Card(
              colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
              shape = RoundedCornerShape(16.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  painter = painterResource(R.drawable.error),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onErrorContainer,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = status.message,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onErrorContainer,
                )
              }
            }
          }
          is LyricsTranslationHelper.TranslationStatus.Success -> {
            Card(
              colors =
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
              shape = RoundedCornerShape(16.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  painter = painterResource(R.drawable.check),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onTertiaryContainer,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = stringResource(R.string.ai_lyrics_translated),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
              }
            }
          }
          is LyricsTranslationHelper.TranslationStatus.Idle -> {}
        }
      }
    }

    if (lyrics == LYRICS_NOT_FOUND && !compact) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
          text = stringResource(R.string.lyrics_not_found),
          fontSize = 20.sp,
          color = MaterialTheme.colorScheme.secondary,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.alpha(0.5f),
        )
      }
    } else if (!compact || isSynced) {
      LazyColumn(
        state = lazyListState,
        userScrollEnabled = !compact,
        contentPadding =
          if (!isSynced) {
            PaddingValues(top = 12.dp, bottom = 24.dp)
          } else if (compact) {
            PaddingValues(bottom = maxHeight)
          } else {
            WindowInsets.systemBars
              .only(WindowInsetsSides.Top)
              .add(WindowInsets(top = maxHeight / 3, bottom = maxHeight / 2))
              .asPaddingValues()
          },
        modifier =
          Modifier.fillMaxSize()
            .clipToBounds()
            .graphicsLayer { alpha = if (presentation.ready) 1f else 0f }
            .then(
              if (compact) Modifier.fadingEdge(bottom = 64.dp)
              else Modifier.fadingEdge(vertical = 64.dp)
            )
            .nestedScroll(
              remember {
                object : NestedScrollConnection {
                  override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                  ): Offset {
                    if (source == NestedScrollSource.UserInput && consumed.y != 0f) {
                      isAutoScrollEnabled = false
                      lastManualScrollTime = System.currentTimeMillis()
                    }
                    if (
                      source == NestedScrollSource.UserInput &&
                        consumed.y != 0f &&
                        !isSelectionModeActive
                    ) {
                      lastPreviewTime = System.currentTimeMillis()
                    }
                    return super.onPostScroll(consumed, available, source)
                  }

                  override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity,
                  ): Velocity {
                    // A programmatic animateScroll also dispatches post-fling. It must not
                    // disable its own follow mode or restart the manual-scroll timeout.
                    if (!isAutoScrollEnabled) {
                      lastManualScrollTime = System.currentTimeMillis()
                      if (!isSelectionModeActive) lastPreviewTime = lastManualScrollTime
                    }
                    return super.onPostFling(consumed, available)
                  }
                }
              }
            ),
      ) {
        val displayedCurrentLineIndex =
          if (!isAutoScrollEnabled) {
            currentLineIndex
          } else {
            if (isSelectionModeActive) deferredCurrentLineIndex else currentLineIndex
          }

        if (!isSynced && lyrics != null) {
          item {
            Text(
              stringResource(R.string.kai_lyrics_untimed),
              color = contentColor.copy(alpha = .65f),
              style = MaterialTheme.typography.labelMedium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
          }
        }
        if (isLyricsProviderShown) {
          item {
            Text(
              text = "Lyrics from ${lyricsEntity?.provider}",
              fontSize = 12.sp,
              color = contentColor.copy(alpha = 0.6f),
              fontWeight = FontWeight.Medium,
              modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            )
          }
        }

        if (lyrics == null) {
          item {
            ShimmerHost {
              repeat(10) {
                Box(
                  contentAlignment =
                    when (lyricsTextPosition) {
                      LyricsPosition.LEFT -> Alignment.CenterStart
                      LyricsPosition.CENTER -> Alignment.Center
                      LyricsPosition.RIGHT -> Alignment.CenterEnd
                    },
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                ) {
                  TextPlaceholder()
                }
              }
            }
          }
        } else {
          val lyricsOffset = currentSong?.song?.lyricsOffset?.toLong() ?: 0L

          itemsIndexed(items = lines, key = { index, item -> "$index-${item.time}" }) { index, item
            ->
            val isSelected = selectedIndices.contains(index)
            // Older letter/Metro renderers estimated word timings or ran a second clock.
            // Keep their saved choices, using the shared safe renderer and original source text.
            val safeLegacyStyle =
              when (lyricsAnimationStyle) {
                LyricsAnimationStyle.echomusic_1 -> LyricsPresentationStyle.FLOATING
                LyricsAnimationStyle.METRO_LYRICS -> LyricsPresentationStyle.CLASSIC_FLOW
                LyricsAnimationStyle.APPLE_V2,
                LyricsAnimationStyle.LYRICS_V2 -> LyricsPresentationStyle.KINETIC
                else -> null
              }
            if (
              presentation.style != LyricsPresentationStyle.CLASSIC_FLOW || safeLegacyStyle != null
            ) {
              val activeTime = lines.getOrNull(displayedCurrentLineIndex)?.time ?: -1L
              KaiLyricsLine(
                entry = item,
                style =
                  if (presentation.style == LyricsPresentationStyle.CLASSIC_FLOW) safeLegacyStyle!!
                  else presentation.style,
                position = playbackClock.position,
                lyricsOffsetMs = lyricsOffset,
                currentLineTimeMs = activeTime,
                nextLineTimeMs = lines.getOrNull(index + 1)?.time,
                distance = index - displayedCurrentLineIndex,
                active = isSynced && displayedCurrentLineIndex >= 0 && item.time == activeTime,
                synced = isSynced,
                previewing = !isAutoScrollEnabled,
                reducedMotion = reduceMotion,
                textPosition = lyricsTextPosition,
                textColor = textColor,
                textSize = lyricsTextSize,
                typeface = lyricsTypeface,
                glow = lyricsGlowEffect,
                lineSpacing = lyricsLineSpacing,
                romanizeAsMain = romanizeAsMain,
                showTranslated = hasActiveTranslations,
                selected = isSelected,
                selectionMode = isSelectionModeActive,
                onClick = {
                  if (isSelectionModeActive) {
                    if (isSelected) {
                      selectedIndices.remove(index)
                      if (selectedIndices.isEmpty()) isSelectionModeActive = false
                    } else if (selectedIndices.size < maxSelectionLimit) {
                      selectedIndices.add(index)
                    } else showMaxSelectionToast = true
                  } else if (isSynced && changeLyrics && !isGuest && item.text.isNotBlank()) {
                    playerConnection.seekTo((item.time - lyricsOffset).coerceAtLeast(0L))
                    currentLineIndex = index
                    lastPreviewTime = 0L
                    scope.launch { performSmoothPageScroll(index, 300) }
                  }
                },
                onLongClick = {
                  if (!isSelectionModeActive) {
                    isSelectionModeActive = true
                    selectedIndices.add(index)
                  } else if (!isSelected && selectedIndices.size < maxSelectionLimit) {
                    selectedIndices.add(index)
                  } else if (!isSelected) showMaxSelectionToast = true
                },
              )
              return@itemsIndexed
            }
            val isPrevSelected = selectedIndices.contains(index - 1)
            val isNextSelected = selectedIndices.contains(index + 1)
            val cornerShape =
              if (isSelected && isSelectionModeActive) {
                androidx.compose.foundation.shape.RoundedCornerShape(
                  topStart = if (isPrevSelected) 0.dp else 16.dp,
                  topEnd = if (isPrevSelected) 0.dp else 16.dp,
                  bottomStart = if (isNextSelected) 0.dp else 16.dp,
                  bottomEnd = if (isNextSelected) 0.dp else 16.dp,
                )
              } else {
                androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
              }

            val itemModifier =
              Modifier.fillMaxWidth()
                .clip(cornerShape)
                .combinedClickable(
                  enabled = true,
                  onClick = {
                    if (isSelectionModeActive) {

                      if (isSelected) {
                        selectedIndices.remove(index)
                        if (selectedIndices.isEmpty()) {
                          isSelectionModeActive = false
                        }
                      } else {
                        if (selectedIndices.size < maxSelectionLimit) {
                          selectedIndices.add(index)
                        } else {
                          showMaxSelectionToast = true
                        }
                      }
                    } else if (isSynced && changeLyrics && !isGuest) {

                      val lyricsOffset = currentSong?.song?.lyricsOffset ?: 0
                      playerConnection.seekTo((item.time - lyricsOffset).coerceAtLeast(0))

                      scope.launch {
                        lazyListState.scrollToItem(index = index)

                        val itemInfo =
                          lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                            it.index == index
                          }
                        if (itemInfo != null) {
                          val viewportHeight =
                            lazyListState.layoutInfo.viewportEndOffset -
                              lazyListState.layoutInfo.viewportStartOffset
                          val center =
                            lazyListState.layoutInfo.viewportStartOffset + (viewportHeight / 2)
                          val itemCenter = itemInfo.offset + itemInfo.size / 2
                          val offset = itemCenter - center

                          if (kotlin.math.abs(offset) > 10) {
                            lazyListState.animateScrollBy(
                              value = offset.toFloat(),
                              animationSpec = tween(durationMillis = 1500),
                            )
                          }
                        }
                      }
                      lastPreviewTime = 0L
                    }
                  },
                  onLongClick = {
                    if (!isSelectionModeActive) {
                      isSelectionModeActive = true
                      selectedIndices.add(index)
                    } else if (!isSelected && selectedIndices.size < maxSelectionLimit) {

                      selectedIndices.add(index)
                    } else if (!isSelected) {

                      showMaxSelectionToast = true
                    }
                  },
                )
                .background(
                  if (isSelected && isSelectionModeActive)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                  else Color.Transparent
                )
                .padding(horizontal = 24.dp, vertical = (8 * lyricsLineSpacing).dp)

            val currentLineTime =
              if (displayedCurrentLineIndex >= 0 && displayedCurrentLineIndex < lines.size) {
                lines[displayedCurrentLineIndex].time
              } else -1L
            val isLineAtSameTime = item.time == currentLineTime
            val isActiveByIndex = index == displayedCurrentLineIndex
            val isActiveByTime = isLineAtSameTime && displayedCurrentLineIndex >= 0
            // Only the active Classic row needs word/glow updates; context text is static.
            val activeClassic = (isActiveByIndex || isActiveByTime) && isSynced
            val genuineWordSpans =
              remember(item.text, item.words) { sourceLyricSpans(item.text, item.words) }
            val effectivePlaybackPosition =
              if (activeClassic && genuineWordSpans.isNotEmpty())
                playbackClock.position.value + lyricsOffset
              else currentLineTime

            val alpha by
              animateFloatAsState(
                targetValue =
                  when {
                    !isSynced || (isSelectionModeActive && isSelected) -> 1f
                    isActiveByIndex || isActiveByTime -> 1f
                    else -> 0.5f
                  },
                animationSpec =
                  spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                  ),
              )
            val scale by
              animateFloatAsState(
                targetValue = if (isActiveByIndex || isActiveByTime) 1.05f else 1f,
                animationSpec =
                  spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                  ),
              )

            val targetBlur =
              if (
                !lyricsStandardBlur ||
                  !isAutoScrollEnabled ||
                  (isSelectionModeActive && isSelected) ||
                  isActiveByIndex ||
                  isActiveByTime
              ) {
                0f
              } else {
                val distance =
                  kotlin.math.abs(
                    index -
                      (if (displayedCurrentLineIndex >= 0) displayedCurrentLineIndex
                      else currentLineIndex)
                  )
                when (distance) {
                  1 -> 0f
                  2 -> 0f
                  3 -> 2f
                  4 -> 4f
                  else -> 6f
                }
              }

            val blurRadius by
              animateFloatAsState(
                targetValue = targetBlur,
                animationSpec =
                  spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                  ),
                label = "standard_blur",
              )

            val agentAlignment =
              when {
                item.isBackground -> Alignment.CenterHorizontally
                item.agent == "v1" -> Alignment.Start
                item.agent == "v2" -> Alignment.End
                item.agent == "v1000" -> Alignment.CenterHorizontally
                else ->
                  when (lyricsTextPosition) {
                    LyricsPosition.LEFT -> Alignment.Start
                    LyricsPosition.CENTER -> Alignment.CenterHorizontally
                    LyricsPosition.RIGHT -> Alignment.End
                  }
              }

            val agentTextAlign =
              when {
                item.isBackground -> TextAlign.Center
                item.agent == "v1" -> TextAlign.Left
                item.agent == "v2" -> TextAlign.Right
                item.agent == "v1000" -> TextAlign.Center
                else ->
                  when (lyricsTextPosition) {
                    LyricsPosition.LEFT -> TextAlign.Left
                    LyricsPosition.CENTER -> TextAlign.Center
                    LyricsPosition.RIGHT -> TextAlign.Right
                  }
              }

            val bgScale = if (item.isBackground) 0.85f else 1f

            Column(
              modifier =
                itemModifier
                  .graphicsLayer {
                    this.alpha = if (item.isBackground) alpha * 0.8f else alpha
                    this.scaleX = scale * bgScale
                    this.scaleY = scale * bgScale
                  }
                  .blur(blurRadius.dp),
              horizontalAlignment = agentAlignment,
            ) {
              val isActiveLine = (isActiveByIndex || isActiveByTime) && isSynced
              ProvideTextStyle(
                LocalTextStyle.current.copy(
                  fontFamily = lyricsTypeface,
                  shadow = lyricGlowShadow(expressiveAccent, lyricsGlowEffect && isActiveLine),
                )
              ) {
                val lineColor =
                  if (isActiveLine) {
                    if (item.isBackground) expressiveAccent.copy(alpha = 0.85f)
                    else expressiveAccent
                  } else {
                    expressiveAccent.copy(alpha = if (item.isBackground) 0.5f else 0.7f)
                  }
                val alignment = agentTextAlign

                val romanizedTextState by item.romanizedTextFlow.collectAsState()
                val romanizedText = romanizedTextState
                val isRomanizedAvailable = romanizedText != null

                val mainText =
                  if (romanizeAsMain && isRomanizedAvailable) romanizedText!! else item.text
                val subText =
                  if (romanizeAsMain && isRomanizedAvailable) item.text else romanizedText

                val hasWordTimings =
                  !(romanizeAsMain && isRomanizedAvailable) && genuineWordSpans.isNotEmpty()

                if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.NONE) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine &&
                          effectivePlaybackPosition >= wordStartMs &&
                          effectivePlaybackPosition <= wordEndMs
                      val hasWordPassed = isActiveLine && effectivePlaybackPosition > wordEndMs

                      val transitionProgress =
                        when {
                          !isActiveLine -> 0f
                          hasWordPassed -> 1f
                          isWordActive && wordDuration > 0 -> {
                            val elapsed = effectivePlaybackPosition - wordStartMs
                            val linear = (elapsed.toFloat() / wordDuration).coerceIn(0f, 1f)
                            linear * linear * (3f - 2f * linear)
                          }
                          else -> 0f
                        }

                      val wordAlpha =
                        when {
                          !isActiveLine -> 0.7f
                          hasWordPassed -> 1f
                          isWordActive -> 0.5f + (0.5f * transitionProgress)
                          else -> 0.35f
                        }

                      val wordColor = expressiveAccent.copy(alpha = wordAlpha)
                      val wordWeight =
                        when {
                          !isActiveLine -> FontWeight.Bold
                          hasWordPassed -> FontWeight.Bold
                          isWordActive -> FontWeight.ExtraBold
                          else -> FontWeight.Medium
                        }

                      addStyle(
                        style = SpanStyle(color = wordColor, fontWeight = wordWeight),
                        start = genuineWordSpans[wordIndex].start,
                        end = genuineWordSpans[wordIndex].end,
                      )
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.FADE) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine &&
                          effectivePlaybackPosition >= wordStartMs &&
                          effectivePlaybackPosition <= wordEndMs
                      val hasWordPassed = isActiveLine && effectivePlaybackPosition > wordEndMs

                      val fadeProgress =
                        if (isWordActive && wordDuration > 0) {
                          val timeElapsed = effectivePlaybackPosition - wordStartMs
                          val linear =
                            (timeElapsed.toFloat() / wordDuration.toFloat()).coerceIn(0f, 1f)

                          linear * linear * (3f - 2f * linear)
                        } else if (hasWordPassed) 1f else 0f

                      val wordAlpha =
                        when {
                          !isActiveLine -> 0.55f
                          hasWordPassed -> 1f
                          isWordActive -> 0.4f + (0.6f * fadeProgress)
                          else -> 0.4f
                        }
                      val wordColor = expressiveAccent.copy(alpha = wordAlpha)
                      val wordWeight =
                        when {
                          !isActiveLine -> FontWeight.Bold
                          hasWordPassed -> FontWeight.Bold
                          isWordActive -> FontWeight.ExtraBold
                          else -> FontWeight.Medium
                        }

                      val wordShadow =
                        when {
                          isWordActive && fadeProgress > 0.2f ->
                            Shadow(
                              color = expressiveAccent.copy(alpha = 0.35f * fadeProgress),
                              offset = Offset.Zero,
                              blurRadius = 10f * fadeProgress,
                            )
                          hasWordPassed ->
                            Shadow(
                              color = expressiveAccent.copy(alpha = 0.15f),
                              offset = Offset.Zero,
                              blurRadius = 6f,
                            )
                          else -> null
                        }

                      addStyle(
                        style =
                          SpanStyle(
                            color = wordColor,
                            fontWeight = wordWeight,
                            shadow = wordShadow,
                          ),
                        start = genuineWordSpans[wordIndex].start,
                        end = genuineWordSpans[wordIndex].end,
                      )
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.GLOW) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine && effectivePlaybackPosition in wordStartMs..wordEndMs
                      val hasWordPassed = isActiveLine && effectivePlaybackPosition > wordEndMs

                      val fillProgress =
                        if (isWordActive && wordDuration > 0) {
                          val linear =
                            ((effectivePlaybackPosition - wordStartMs).toFloat() / wordDuration)
                              .coerceIn(0f, 1f)
                          linear * linear * (3f - 2f * linear)
                        } else if (hasWordPassed) 1f else 0f

                      val glowIntensity = fillProgress * fillProgress
                      val brightness = 0.45f + (0.55f * fillProgress)

                      val wordColor =
                        when {
                          !isActiveLine -> expressiveAccent.copy(alpha = 0.5f)
                          isWordActive || hasWordPassed -> expressiveAccent.copy(alpha = brightness)
                          else -> expressiveAccent.copy(alpha = 0.35f)
                        }
                      val wordWeight =
                        when {
                          !isActiveLine -> FontWeight.Bold
                          isWordActive -> FontWeight.ExtraBold
                          hasWordPassed -> FontWeight.Bold
                          else -> FontWeight.Medium
                        }
                      val wordShadow =
                        if (isWordActive && glowIntensity > 0.05f) {
                          Shadow(
                            color = expressiveAccent.copy(alpha = 0.5f + (0.3f * glowIntensity)),
                            offset = Offset.Zero,
                            blurRadius = 16f + (12f * glowIntensity),
                          )
                        } else if (hasWordPassed) {
                          Shadow(
                            color = expressiveAccent.copy(alpha = 0.25f),
                            offset = Offset.Zero,
                            blurRadius = 8f,
                          )
                        } else null

                      addStyle(
                        style =
                          SpanStyle(
                            color = wordColor,
                            fontWeight = wordWeight,
                            shadow = wordShadow,
                          ),
                        start = genuineWordSpans[wordIndex].start,
                        end = genuineWordSpans[wordIndex].end,
                      )
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.SLIDE) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine &&
                          effectivePlaybackPosition >= wordStartMs &&
                          effectivePlaybackPosition < wordEndMs
                      val hasWordPassed =
                        (isActiveLine && effectivePlaybackPosition >= wordEndMs) ||
                          (!isActiveLine && item.time < currentLineTime)

                      if (isWordActive && wordDuration > 0) {
                        val timeElapsed = effectivePlaybackPosition - wordStartMs
                        val fillProgress =
                          (timeElapsed.toFloat() / wordDuration.toFloat()).coerceIn(0f, 1f)
                        val breatheValue = (timeElapsed % 3000) / 3000f
                        val breatheEffect =
                          (kotlin.math.sin(breatheValue * Math.PI.toFloat() * 2f) * 0.03f).coerceIn(
                            0f,
                            0.03f,
                          )
                        val glowIntensity =
                          (0.3f + fillProgress * 0.7f + breatheEffect).coerceIn(0f, 1.1f)

                        val slideBrush =
                          Brush.horizontalGradient(
                            0.0f to expressiveAccent,
                            (fillProgress * 0.95f).coerceIn(0f, 1f) to expressiveAccent,
                            fillProgress to expressiveAccent.copy(alpha = 0.9f),
                            (fillProgress + 0.02f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.5f),
                            (fillProgress + 0.08f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.35f),
                            1.0f to expressiveAccent.copy(alpha = 0.35f),
                          )

                        addStyle(
                          style =
                            SpanStyle(
                              brush = slideBrush,
                              fontWeight = FontWeight.ExtraBold,
                              shadow =
                                Shadow(
                                  color = expressiveAccent.copy(alpha = 0.4f * glowIntensity),
                                  offset = Offset(0f, 0f),
                                  blurRadius = 14f + (4f * fillProgress),
                                ),
                            ),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      } else if (hasWordPassed && isActiveLine) {
                        addStyle(
                          style =
                            SpanStyle(
                              color = expressiveAccent,
                              fontWeight = FontWeight.Bold,
                              shadow =
                                Shadow(
                                  color = expressiveAccent.copy(alpha = 0.4f),
                                  offset = Offset(0f, 0f),
                                  blurRadius = 12f,
                                ),
                            ),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      } else {
                        val wordColor =
                          if (!isActiveLine) lineColor else expressiveAccent.copy(alpha = 0.35f)
                        addStyle(
                          style = SpanStyle(color = wordColor, fontWeight = FontWeight.Medium),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      }
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing).sp,
                  )
                } else if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.KARAOKE) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine &&
                          effectivePlaybackPosition >= wordStartMs &&
                          effectivePlaybackPosition < wordEndMs
                      val hasWordPassed =
                        (isActiveLine && effectivePlaybackPosition >= wordEndMs) ||
                          (!isActiveLine && item.time < currentLineTime)

                      if (isWordActive && wordDuration > 0) {
                        val timeElapsed = effectivePlaybackPosition - wordStartMs
                        val linearProgress =
                          (timeElapsed.toFloat() / wordDuration.toFloat()).coerceIn(0f, 1f)

                        val fillProgress =
                          linearProgress * linearProgress * (3f - 2f * linearProgress)

                        val glowIntensity = fillProgress * fillProgress

                        val wordBrush =
                          Brush.horizontalGradient(
                            0.0f to expressiveAccent.copy(alpha = 0.4f),
                            (fillProgress * 0.6f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.75f),
                            (fillProgress * 0.85f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.95f),
                            fillProgress to expressiveAccent,
                            (fillProgress + 0.03f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.85f),
                            (fillProgress + 0.1f).coerceIn(0f, 1f) to
                              expressiveAccent.copy(alpha = 0.5f),
                            1.0f to
                              expressiveAccent.copy(
                                alpha = if (fillProgress >= 0.9f) 0.95f else 0.4f
                              ),
                          )

                        val wordShadow =
                          Shadow(
                            color = expressiveAccent.copy(alpha = 0.5f + (0.3f * glowIntensity)),
                            offset = Offset.Zero,
                            blurRadius = 16f + (12f * glowIntensity),
                          )

                        addStyle(
                          style =
                            SpanStyle(
                              brush = wordBrush,
                              fontWeight = FontWeight.ExtraBold,
                              shadow = wordShadow,
                            ),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      } else if (hasWordPassed && isActiveLine) {

                        addStyle(
                          style =
                            SpanStyle(
                              color = expressiveAccent,
                              fontWeight = FontWeight.Bold,
                              shadow =
                                Shadow(
                                  color = expressiveAccent.copy(alpha = 0.25f),
                                  offset = Offset.Zero,
                                  blurRadius = 8f,
                                ),
                            ),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      } else {

                        val wordColor =
                          if (!isActiveLine) lineColor else expressiveAccent.copy(alpha = 0.4f)
                        addStyle(
                          style = SpanStyle(color = wordColor, fontWeight = FontWeight.Medium),
                          start = genuineWordSpans[wordIndex].start,
                          end = genuineWordSpans[wordIndex].end,
                        )
                      }
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (hasWordTimings && lyricsAnimationStyle == LyricsAnimationStyle.APPLE) {
                  val styledText = buildAnnotatedString {
                    append(mainText)
                    item.words?.forEachIndexed { wordIndex, word ->
                      val wordStartMs = (word.startTime * 1000).toLong()
                      val wordEndMs = (word.endTime * 1000).toLong()
                      val wordDuration = wordEndMs - wordStartMs

                      val isWordActive =
                        isActiveLine &&
                          effectivePlaybackPosition >= wordStartMs &&
                          effectivePlaybackPosition < wordEndMs
                      val hasWordPassed =
                        (isActiveLine && effectivePlaybackPosition >= wordEndMs) ||
                          (!isActiveLine && item.time < currentLineTime)

                      val rawProgress =
                        if (isWordActive && wordDuration > 0) {
                          val elapsed = effectivePlaybackPosition - wordStartMs
                          (elapsed.toFloat() / wordDuration).coerceIn(0f, 1f)
                        } else if (hasWordPassed) 1f else 0f

                      val smoothProgress = rawProgress * rawProgress * (3f - 2f * rawProgress)

                      val wordAlpha =
                        when {
                          !isActiveLine -> 0.55f
                          hasWordPassed -> 1f
                          isWordActive -> 0.55f + (0.45f * smoothProgress)
                          else -> 0.4f
                        }
                      val wordColor = expressiveAccent.copy(alpha = wordAlpha)
                      val wordWeight =
                        when {
                          !isActiveLine -> FontWeight.SemiBold
                          hasWordPassed -> FontWeight.Bold
                          isWordActive -> FontWeight.ExtraBold
                          else -> FontWeight.Normal
                        }

                      val glowIntensity = smoothProgress * smoothProgress
                      val wordShadow =
                        when {
                          isWordActive ->
                            Shadow(
                              color = expressiveAccent.copy(alpha = 0.2f + (0.4f * glowIntensity)),
                              offset = Offset.Zero,
                              blurRadius = 10f + (12f * glowIntensity),
                            )
                          hasWordPassed && isActiveLine ->
                            Shadow(
                              color = expressiveAccent.copy(alpha = 0.2f),
                              offset = Offset.Zero,
                              blurRadius = 8f,
                            )
                          else -> null
                        }

                      addStyle(
                        style =
                          SpanStyle(
                            color = wordColor,
                            fontWeight = wordWeight,
                            shadow = wordShadow,
                          ),
                        start = genuineWordSpans[wordIndex].start,
                        end = genuineWordSpans[wordIndex].end,
                      )
                    }
                  }
                  Text(
                    fontFamily = lyricsTypeface,
                    text = styledText,
                    fontSize = lyricsTextSize.sp,
                    textAlign = alignment,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (lyricsAnimationStyle == LyricsAnimationStyle.LYRICS_V2) {
                  LyricsLineV2(
                    entry =
                      if (mainText != item.text || !hasWordTimings)
                        item.copy(text = mainText, words = null)
                      else item,
                    isActive = isActiveLine,
                    isPast = !isActiveLine && item.time < currentLineTime,
                    effectivePlaybackPosition = effectivePlaybackPosition,
                    expressiveAccent = expressiveAccent,
                    inactiveAlpha = 0.35f,
                    baseFontSize = lyricsTextSize,
                    lineHeight = lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f),
                    showTranslated = hasActiveTranslations,
                    agentAlignment = agentAlignment,
                    agentTextAlign = agentTextAlign,
                  )
                } else if (isActiveLine && lyricsGlowEffect) {
                  Text(
                    style =
                      LocalTextStyle.current.copy(shadow = lyricGlowShadow(expressiveAccent, true)),
                    fontFamily = lyricsTypeface,
                    text = mainText,
                    fontSize = lyricsTextSize.sp,
                    color = expressiveAccent,
                    textAlign = alignment,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else if (isActiveLine && !lyricsGlowEffect) {

                  Text(
                    fontFamily = lyricsTypeface,
                    text = mainText,
                    fontSize = lyricsTextSize.sp,
                    color = expressiveAccent,
                    textAlign = alignment,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                } else {

                  Text(
                    fontFamily = lyricsTypeface,
                    text = mainText,
                    fontSize = lyricsTextSize.sp,
                    color = lineColor,
                    textAlign = alignment,
                    fontWeight = FontWeight.Bold,
                    lineHeight = (lyricsTextSize * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                  )
                }
                if (true) {

                  subText?.let { text ->
                    Text(
                      fontFamily = lyricsTypeface,
                      text = text,
                      fontSize = 18.sp,
                      color = expressiveAccent.copy(alpha = 0.6f),
                      textAlign =
                        when (lyricsTextPosition) {
                          LyricsPosition.LEFT -> TextAlign.Left
                          LyricsPosition.CENTER -> TextAlign.Center
                          LyricsPosition.RIGHT -> TextAlign.Right
                        },
                      fontWeight = FontWeight.Normal,
                      modifier = Modifier.padding(top = 2.dp),
                    )
                  }
                }

                if (
                  hasActiveTranslations &&
                    lyricsAnimationStyle != LyricsAnimationStyle.LYRICS_V2 &&
                    lyricsAnimationStyle != LyricsAnimationStyle.APPLE_V2
                ) {
                  val translatedText by item.translatedTextFlow.collectAsState()
                  translatedText?.let { translated ->
                    Text(
                      fontFamily = lyricsTypeface,
                      text = translated,
                      fontSize = (lyricsTextSize * 0.7f).sp,
                      color = expressiveAccent.copy(alpha = 0.8f),
                      textAlign =
                        when (lyricsTextPosition) {
                          LyricsPosition.LEFT -> TextAlign.Left
                          LyricsPosition.CENTER -> TextAlign.Center
                          LyricsPosition.RIGHT -> TextAlign.Right
                        },
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                      lineHeight =
                        (lyricsTextSize * 0.7f * lyricsLineSpacing.coerceAtMost(1.3f)).sp,
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
      AnimatedVisibility(
        visible = !isAutoScrollEnabled && isSynced && !isSelectionModeActive,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
      ) {
        FilledTonalButton(
          onClick = {
            scope.launch { performSmoothPageScroll(currentLineIndex, 1500) }
            isAutoScrollEnabled = true
          }
        ) {
          Icon(
            painter = painterResource(id = R.drawable.sync),
            contentDescription = stringResource(R.string.auto_scroll),
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = stringResource(R.string.auto_scroll))
        }
      }

      AnimatedVisibility(
        visible = isSelectionModeActive,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          FilledTonalButton(
            onClick = {
              isSelectionModeActive = false
              selectedIndices.clear()
            }
          ) {
            Icon(
              painter = painterResource(id = R.drawable.close),
              contentDescription = stringResource(R.string.cancel),
              modifier = Modifier.size(20.dp),
            )
          }
          FilledTonalButton(
            onClick = {
              if (selectedIndices.isNotEmpty()) {
                val sortedIndices = selectedIndices.sorted()
                val selectedLyricsText =
                  sortedIndices.mapNotNull { lines.getOrNull(it)?.text }.joinToString("\n")

                if (selectedLyricsText.isNotBlank()) {
                  shareDialogData =
                    Triple(
                      selectedLyricsText,
                      mediaMetadata?.title ?: "",
                      mediaMetadata?.artists?.joinToString { it.name } ?: "",
                    )
                  showShareDialog = true
                }
                isSelectionModeActive = false
                selectedIndices.clear()
              }
            },
            enabled = selectedIndices.isNotEmpty(),
          ) {
            Icon(
              painter = painterResource(id = R.drawable.share),
              contentDescription = stringResource(R.string.share_selected),
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.share))
          }
        }
      }
    }

    if (showProgressDialog) {
      BasicAlertDialog(onDismissRequest = {}) {
        Card(
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
          Box(modifier = Modifier.padding(32.dp)) {
            Text(
              text =
                stringResource(R.string.generating_image) +
                  "\n" +
                  stringResource(R.string.please_wait),
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }
    }

    if (showShareDialog && shareDialogData != null) {
      val (lyricsText, songTitle, artists) = shareDialogData!!
      BasicAlertDialog(onDismissRequest = { showShareDialog = false }) {
        Card(
          shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
          colors =
            CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
          modifier = Modifier.fillMaxWidth(0.9f),
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              text = stringResource(R.string.share_lyrics),
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.FilledTonalButton(
              onClick = {
                val shareIntent =
                  Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    val songLink = "https://music.youtube.com/watch?v=${mediaMetadata?.id}"
                    putExtra(
                      Intent.EXTRA_TEXT,
                      "\"$lyricsText\"\n\n$songTitle - $artists\n$songLink",
                    )
                  }
                context.startActivity(
                  Intent.createChooser(shareIntent, context.getString(R.string.share_lyrics))
                )
                showShareDialog = false
              },
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(text = stringResource(R.string.share_as_text), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.FilledTonalButton(
              onClick = {
                shareDialogData = Triple(lyricsText, songTitle, artists)
                showColorPickerDialog = true
                showShareDialog = false
              },
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(text = stringResource(R.string.share_as_image), fontSize = 16.sp)
            }

            Row(
              modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 0.dp),
              horizontalArrangement = Arrangement.End,
            ) {
              androidx.compose.material3.TextButton(
                onClick = { showShareDialog = false },
                modifier = Modifier.padding(end = 4.dp),
              ) {
                Text(
                  text = stringResource(R.string.cancel),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.error,
                )
              }
            }
          }
        }
      }
    }

    if (showColorPickerDialog && shareDialogData != null) {
      val (lyricsText, songTitle, artists) = shareDialogData!!
      val coverUrl = mediaMetadata?.thumbnailUrl
      val paletteColors = remember { mutableStateListOf<Color>() }

      var previewBackgroundStyle by remember { mutableStateOf(LyricsBackgroundStyle.SOLID) }

      val previewCardWidth = configuration.containerDpSize.width * 0.90f
      val previewPadding = 20.dp * 2
      val previewBoxPadding = 28.dp * 2
      val previewAvailableWidth = previewCardWidth - previewPadding - previewBoxPadding
      val previewBoxHeight = 340.dp
      val headerFooterEstimate = (48.dp + 14.dp + 16.dp + 20.dp + 8.dp + 28.dp * 2)
      val previewAvailableHeight = previewBoxHeight - headerFooterEstimate

      val lyricsTextAlign =
        when (lyricsTextPosition) {
          LyricsPosition.LEFT -> TextAlign.Left
          LyricsPosition.CENTER -> TextAlign.Center
          LyricsPosition.RIGHT -> TextAlign.Right
        }

      val textStyleForMeasurement =
        TextStyle(
          color = previewTextColor,
          fontWeight = FontWeight.Bold,
          textAlign = lyricsTextAlign,
        )
      val textMeasurer = rememberTextMeasurer()

      rememberAdjustedFontSize(
        text = lyricsText,
        maxWidth = previewAvailableWidth,
        maxHeight = previewAvailableHeight,
        density = density,
        initialFontSize = 50.sp,
        minFontSize = 22.sp,
        style = textStyleForMeasurement,
        textMeasurer = textMeasurer,
      )

      LaunchedEffect(coverUrl) {
        if (coverUrl != null) {
          withContext(Dispatchers.IO) {
            try {
              val loader = context.imageLoader
              val req = ImageRequest.Builder(context).data(coverUrl).allowHardware(false).build()
              val result = loader.execute(req)
              val bmp = result.image?.toBitmap()
              if (bmp != null) {
                val palette = Palette.from(bmp).generate()
                val swatches = palette.swatches.sortedByDescending { it.population }
                val colors =
                  swatches
                    .map { Color(it.rgb) }
                    .filter { color ->
                      val hsv = FloatArray(3)
                      android.graphics.Color.colorToHSV(color.toArgb(), hsv)
                      hsv[1] > 0.2f
                    }
                paletteColors.clear()
                paletteColors.addAll(colors.take(5))
              }
            } catch (_: Exception) {}
          }
        }
      }

      androidx.compose.ui.window.Dialog(
        onDismissRequest = { showColorPickerDialog = false },
        properties =
          androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
          ),
      ) {
        Box(
          modifier =
            Modifier.fillMaxSize()
              .padding(WindowInsets.systemBars.asPaddingValues())
              .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          Card(
            shape = RoundedCornerShape(28.dp),
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
              ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier =
              Modifier.fillMaxWidth()
                // Ensure the card is constrained so the inner column can scroll
                .heightIn(max = 650.dp),
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier =
                Modifier.fillMaxWidth()
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 24.dp, vertical = 28.dp),
            ) {
              Text(
                text = stringResource(id = R.string.customize_colors),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
              )

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = stringResource(id = R.string.player_background_style),
                style = MaterialTheme.typography.titleMedium,
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp),
              ) {
                LyricsBackgroundStyle.entries.forEach { style ->
                  val label =
                    when (style) {
                      LyricsBackgroundStyle.SOLID ->
                        stringResource(R.string.player_background_solid)
                      LyricsBackgroundStyle.BLUR -> stringResource(R.string.player_background_blur)
                      LyricsBackgroundStyle.GRADIENT -> stringResource(R.string.gradient)
                    }
                  val selected = previewBackgroundStyle == style

                  androidx.compose.material3.FilterChip(
                    selected = selected,
                    onClick = { previewBackgroundStyle = style },
                    label = { Text(label) },
                  )
                }
              }

              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
              ) {
                LyricsImageCard(
                  lyricText = lyricsText,
                  mediaMetadata = mediaMetadata ?: return@Box,
                  backgroundColor = previewBackgroundColor,
                  backgroundStyle = previewBackgroundStyle,
                  textColor = previewTextColor,
                  secondaryTextColor = previewSecondaryTextColor,
                  textAlign = lyricsTextAlign,
                )
              }

              Spacer(modifier = Modifier.height(24.dp))

              Text(
                text = stringResource(id = R.string.background_color),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Start,
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier =
                  Modifier.fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
              ) {
                (paletteColors +
                    listOf(
                      Color(0xFF242424),
                      Color(0xFF121212),
                      Color.White,
                      Color.Black,
                      Color(0xFFF5F5F5),
                      Color(0xFFEC5464),
                      Color(0xFF039BE5),
                      Color(0xFF43A047),
                      Color(0xFF8E24AA),
                    ))
                  .distinct()
                  .take(12)
                  .forEach { color ->
                    Box(
                      modifier =
                        Modifier.size(40.dp)
                          .clip(androidx.compose.foundation.shape.CircleShape)
                          .background(color)
                          .clickable { previewBackgroundColor = color }
                          .border(
                            width = if (previewBackgroundColor == color) 3.dp else 1.dp,
                            color =
                              if (previewBackgroundColor == color) MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                          )
                    )
                  }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = stringResource(id = R.string.text_color),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Start,
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier =
                  Modifier.fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
              ) {
                (paletteColors +
                    listOf(
                      Color.White,
                      Color.Black,
                      Color(0xFF1DB954),
                      Color(0xFFEC5464),
                      Color(0xFF039BE5),
                      Color(0xFFFFB300),
                    ))
                  .distinct()
                  .take(12)
                  .forEach { color ->
                    Box(
                      modifier =
                        Modifier.size(40.dp)
                          .clip(androidx.compose.foundation.shape.CircleShape)
                          .background(color)
                          .clickable { previewTextColor = color }
                          .border(
                            width = if (previewTextColor == color) 3.dp else 1.dp,
                            color =
                              if (previewTextColor == color) MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                          )
                    )
                  }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = stringResource(id = R.string.secondary_text_color),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Start,
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier =
                  Modifier.fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
              ) {
                (paletteColors.map { it.copy(alpha = 0.7f) } +
                    listOf(
                      Color.White.copy(alpha = 0.7f),
                      Color.Black.copy(alpha = 0.7f),
                      Color(0xFF1DB954).copy(alpha = 0.7f),
                      Color(0xFFEC5464).copy(alpha = 0.7f),
                      Color(0xFF039BE5).copy(alpha = 0.7f),
                    ))
                  .distinct()
                  .take(12)
                  .forEach { color ->
                    Box(
                      modifier =
                        Modifier.size(40.dp)
                          .clip(androidx.compose.foundation.shape.CircleShape)
                          .background(color)
                          .clickable { previewSecondaryTextColor = color }
                          .border(
                            width = if (previewSecondaryTextColor == color) 3.dp else 1.dp,
                            color =
                              if (previewSecondaryTextColor == color)
                                MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                          )
                    )
                  }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  showColorPickerDialog = false
                  showProgressDialog = true
                  scope.launch {
                    try {
                      val screenWidth = configuration.containerSize.width
                      val screenHeight = configuration.containerSize.height

                      val image =
                        ComposeToImage.createLyricsImage(
                          context = context,
                          coverArtUrl = coverUrl,
                          songTitle = songTitle,
                          artistName = artists,
                          lyrics = lyricsText,
                          width = (screenWidth * density.density).toInt(),
                          height = (screenHeight * density.density).toInt(),
                          backgroundColor = previewBackgroundColor.toArgb(),
                          backgroundStyle = previewBackgroundStyle,
                          textColor = previewTextColor.toArgb(),
                          secondaryTextColor = previewSecondaryTextColor.toArgb(),
                          lyricsAlignment =
                            when (lyricsTextPosition) {
                              LyricsPosition.LEFT -> Layout.Alignment.ALIGN_NORMAL
                              LyricsPosition.CENTER -> Layout.Alignment.ALIGN_CENTER
                              LyricsPosition.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
                            },
                        )
                      val timestamp = System.currentTimeMillis()
                      val filename = "lyrics_$timestamp"
                      val uri = ComposeToImage.saveBitmapAsFile(context, image, filename)
                      val shareIntent =
                        Intent(Intent.ACTION_SEND).apply {
                          type = "image/png"
                          putExtra(Intent.EXTRA_STREAM, uri)
                          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                      context.startActivity(
                        Intent.createChooser(shareIntent, context.getString(R.string.share_lyrics))
                      )
                    } catch (e: Exception) {
                      Toast.makeText(
                          context,
                          context.getString(R.string.failed_to_create_image, e.message),
                          Toast.LENGTH_SHORT,
                        )
                        .show()
                    } finally {
                      showProgressDialog = false
                    }
                  }
                },
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(stringResource(id = R.string.share))
              }

              Spacer(
                modifier =
                  Modifier.padding(
                    bottom =
                      androidx.compose.foundation.layout.WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                  )
              )
            }
          }
        }
      } // closes Dialog
    }
  }
}

private const val echomusic_AUTO_SCROLL_DURATION = 1500L
private const val echomusic_INITIAL_SCROLL_DURATION = 1000L
private const val echomusic_SEEK_DURATION = 800L
private const val echomusic_FAST_SEEK_DURATION = 600L

val LyricsPreviewTime = 2.seconds
