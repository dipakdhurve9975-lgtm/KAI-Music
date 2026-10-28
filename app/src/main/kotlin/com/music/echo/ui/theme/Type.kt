/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R

@OptIn(ExperimentalTextApi::class)
val GoogleSansFontFamily =
  FontFamily(
    Font(
      resId = R.font.google_sans_flex,
      weight = FontWeight.Normal,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(400),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
    Font(
      resId = R.font.google_sans_flex,
      weight = FontWeight.Medium,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(500),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
    Font(
      resId = R.font.google_sans_flex,
      weight = FontWeight.Bold,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(700),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
  )

@OptIn(ExperimentalTextApi::class)
val SansFlexFontFamily =
  FontFamily(
    Font(
      resId = R.font.sans_flex,
      weight = FontWeight.Normal,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(400),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
    Font(
      resId = R.font.sans_flex,
      weight = FontWeight.Medium,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(500),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
    Font(
      resId = R.font.sans_flex,
      weight = FontWeight.Bold,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(700),
          FontVariation.width(100f),
          FontVariation.Setting("ROND", 100f),
        ),
    ),
  )

@OptIn(ExperimentalTextApi::class)
val OutfitFontFamily =
  FontFamily(
    Font(
      resId = R.font.outfit,
      weight = FontWeight.Normal,
      variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
      resId = R.font.outfit,
      weight = FontWeight.Medium,
      variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
      resId = R.font.outfit,
      weight = FontWeight.Bold,
      variationSettings = FontVariation.Settings(FontVariation.weight(700)),
    ),
  )

@OptIn(ExperimentalTextApi::class)
val PlusJakartaSansFontFamily =
  FontFamily(
    Font(
      resId = R.font.plus_jakarta_sans,
      weight = FontWeight.Normal,
      variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
      resId = R.font.plus_jakarta_sans,
      weight = FontWeight.Medium,
      variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
      resId = R.font.plus_jakarta_sans,
      weight = FontWeight.SemiBold,
      variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
    Font(
      resId = R.font.plus_jakarta_sans,
      weight = FontWeight.Bold,
      variationSettings = FontVariation.Settings(FontVariation.weight(700)),
    ),
  )

/**
 * KAI's single type scale. Body text uses the selected family too, with platform glyph fallback.
 */
fun getTypography(brandFont: FontFamily, plainFont: FontFamily = brandFont): Typography =
  Typography(
    displayLarge = kaiTextStyle(brandFont, 56, 64, FontWeight.Bold, -1.4f),
    displayMedium = kaiTextStyle(brandFont, 46, 54, FontWeight.Bold, -0.9f),
    displaySmall = kaiTextStyle(brandFont, 38, 46, FontWeight.Bold, -0.7f),
    headlineLarge = kaiTextStyle(brandFont, 36, 44, FontWeight.Bold, -0.8f),
    headlineMedium = kaiTextStyle(brandFont, 30, 38, FontWeight.Bold, -0.5f),
    headlineSmall = kaiTextStyle(brandFont, 24, 32, FontWeight.SemiBold, -0.3f),
    titleLarge = kaiTextStyle(brandFont, 23, 30, FontWeight.SemiBold, -0.25f),
    titleMedium = kaiTextStyle(brandFont, 18, 25, FontWeight.SemiBold, -0.15f),
    titleSmall = kaiTextStyle(brandFont, 15, 21, FontWeight.SemiBold),
    bodyLarge = kaiTextStyle(plainFont, 16, 25, FontWeight.Normal, 0.05f),
    bodyMedium = kaiTextStyle(plainFont, 15, 23, FontWeight.Normal, 0.1f),
    bodySmall = kaiTextStyle(plainFont, 13, 19, FontWeight.Normal, 0.15f),
    labelLarge = kaiTextStyle(plainFont, 14, 20, FontWeight.Medium, 0.05f),
    labelMedium = kaiTextStyle(plainFont, 12, 18, FontWeight.Medium, 0.15f),
    labelSmall = kaiTextStyle(plainFont, 11, 16, FontWeight.Medium, 0.15f),
  )

private fun kaiTextStyle(
  family: FontFamily,
  size: Int,
  lineHeight: Int,
  weight: FontWeight,
  tracking: Float = 0f,
) =
  TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
  )

val AppTypography = getTypography(PlusJakartaSansFontFamily)
