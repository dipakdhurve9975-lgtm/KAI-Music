package echo.music.iad1tya.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score
import echo.music.iad1tya.constants.AppFont
import echo.music.iad1tya.constants.SelectedFontKey
import echo.music.iad1tya.utils.rememberPreference

val DefaultThemeColor = KaiPalette.Rose
/** Keep the original system-palette sentinel so upgrades retain their chosen colors. */
val SystemThemeColor = Color(0xFFED5564)

private val KaiDarkColorScheme =
  darkColorScheme(
    primary = KaiPalette.Rose,
    onPrimary = Color(0xFF310916),
    primaryContainer = Color(0xFF4C2533),
    onPrimaryContainer = Color(0xFFFFD9E2),
    inversePrimary = KaiPalette.LightAccent,
    secondary = KaiPalette.SoftRose,
    onSecondary = Color(0xFF45202B),
    secondaryContainer = Color(0xFF3C2A34),
    onSecondaryContainer = Color(0xFFF9DCE6),
    tertiary = Color(0xFFD1BEDF),
    onTertiary = Color(0xFF382845),
    tertiaryContainer = Color(0xFF483654),
    onTertiaryContainer = Color(0xFFF0DEFF),
    background = KaiPalette.DarkBackground,
    onBackground = KaiPalette.DarkText,
    surface = KaiPalette.DarkBackground,
    onSurface = KaiPalette.DarkText,
    surfaceVariant = KaiPalette.DarkElevated,
    onSurfaceVariant = KaiPalette.DarkSecondaryText,
    surfaceTint = KaiPalette.Rose,
    inverseSurface = Color(0xFFF0E9EC),
    inverseOnSurface = Color(0xFF342A32),
    outline = Color(0xFF82737C),
    outlineVariant = Color(0xFF40343D),
    surfaceBright = Color(0xFF3A3038),
    surfaceDim = KaiPalette.DarkBackground,
    surfaceContainerLowest = Color(0xFF0B080B),
    surfaceContainerLow = Color(0xFF171217),
    surfaceContainer = KaiPalette.DarkSurface,
    surfaceContainerHigh = KaiPalette.DarkElevated,
    surfaceContainerHighest = Color(0xFF342A32),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
  )

private val KaiLightColorScheme =
  lightColorScheme(
    primary = KaiPalette.LightAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E3),
    onPrimaryContainer = Color(0xFF3C1020),
    inversePrimary = KaiPalette.SoftRose,
    secondary = Color(0xFF775361),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3DAE3),
    onSecondaryContainer = Color(0xFF2E1520),
    tertiary = Color(0xFF6D5C83),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF0DEFF),
    onTertiaryContainer = Color(0xFF271436),
    background = KaiPalette.LightBackground,
    onBackground = KaiPalette.LightText,
    surface = KaiPalette.LightBackground,
    onSurface = KaiPalette.LightText,
    surfaceVariant = KaiPalette.LightElevated,
    onSurfaceVariant = KaiPalette.LightSecondaryText,
    surfaceTint = KaiPalette.LightAccent,
    inverseSurface = Color(0xFF342A32),
    inverseOnSurface = Color(0xFFF8F0F3),
    outline = Color(0xFF8A7983),
    outlineVariant = Color(0xFFDCCED5),
    surfaceBright = KaiPalette.LightBackground,
    surfaceDim = Color(0xFFE7DCE0),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFF4F6),
    surfaceContainer = KaiPalette.LightSurface,
    surfaceContainerHigh = KaiPalette.LightElevated,
    surfaceContainerHighest = Color(0xFFE8DBE1),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
  )

@Composable
fun echomusicTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  pureBlack: Boolean = false,
  themeColor: Color = DefaultThemeColor,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val selectedFontValue by rememberPreference(SelectedFontKey, AppFont.PLUS_JAKARTA_SANS.value)
  val customFontPathValue by rememberPreference(echo.music.iad1tya.constants.CustomFontPathKey, "")

  val brandFont =
    remember(selectedFontValue, customFontPathValue) {
      when (AppFont.fromValue(selectedFontValue)) {
        AppFont.SYSTEM -> FontFamily.Default
        AppFont.GOOGLE_SANS -> GoogleSansFontFamily
        AppFont.SANS_FLEX -> SansFlexFontFamily
        AppFont.OUTFIT -> OutfitFontFamily
        AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
        AppFont.CUSTOM -> {
          try {
            if (customFontPathValue.isNotEmpty() && java.io.File(customFontPathValue).exists()) {
              val typeface = android.graphics.Typeface.createFromFile(customFontPathValue)
              FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
            } else {
              FontFamily.Default
            }
          } catch (e: Exception) {
            FontFamily.Default
          }
        }
        else -> FontFamily.Default
      }
    }

  // Brand and system palettes have stable, distinct persisted values. Other values remain
  // custom/artwork seeds, so no preference migration or additional observer is required.
  val isDefaultSeed = themeColor == DefaultThemeColor || themeColor == SystemThemeColor
  val useSystemDynamicColor =
    themeColor == SystemThemeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

  val baseColorScheme =
    if (useSystemDynamicColor) {

      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (isDefaultSeed) {
      if (darkTheme) KaiDarkColorScheme else KaiLightColorScheme
    } else {

      rememberDynamicColorScheme(
        seedColor = themeColor,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.TonalSpot,
      )
    }

  val colorScheme =
    remember(baseColorScheme, pureBlack, darkTheme) {
      if (darkTheme && pureBlack) {
        baseColorScheme.pureBlack(true)
      } else {
        baseColorScheme
      }
    }

  val reducedMotion = rememberKaiSystemReducedMotion()
  CompositionLocalProvider(LocalKaiReducedMotion provides reducedMotion) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = getTypography(brandFont),
      shapes = KaiShapes.theme,
      content = content,
    )
  }
}

fun Bitmap.extractThemeColor(): Color {
  val colorsToPopulation =
    Palette.from(this).maximumColorCount(8).generate().swatches.associate {
      it.rgb to it.population
    }
  val rankedColors = Score.score(colorsToPopulation, 4, DefaultThemeColor.toArgb(), true)
  return Color(rankedColors.firstOrNull() ?: DefaultThemeColor.toArgb())
}

fun Bitmap.extractGradientColors(): List<Color> {
  val extractedColors =
    Palette.from(this).maximumColorCount(64).generate().swatches.associate {
      it.rgb to it.population
    }

  val orderedColors =
    Score.score(extractedColors, 2, KaiPalette.DarkSurface.toArgb(), true).sortedByDescending {
      Color(it).luminance()
    }

  return if (orderedColors.size >= 2) listOf(Color(orderedColors[0]), Color(orderedColors[1]))
  else listOf(KaiPalette.DarkSurface, KaiPalette.DarkBackground)
}

fun ColorScheme.pureBlack(apply: Boolean) =
  if (apply) copy(surface = Color.Black, background = Color.Black) else this

val ColorSaver =
  object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)

    override fun SaverScope.save(value: Color): Int = value.toArgb()
  }
