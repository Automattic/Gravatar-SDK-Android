package com.gravatar.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal fun gravatarLightColorScheme() = lightColorScheme(
    primary = ColorsPalette.Primary50,
    onPrimary = ColorsPalette.Neutral100,
    secondary = ColorsPalette.Secondary50,
    onSecondary = ColorsPalette.Neutral100,
    tertiary = ColorsPalette.Neutral70,
    onTertiary = ColorsPalette.Neutral100,
    error = ColorsPalette.Error50,
    onError = ColorsPalette.Error70,
    errorContainer = ColorsPalette.Error70,
    onErrorContainer = ColorsPalette.Secondary50,
    surfaceDim = ColorsPalette.Neutral90,
    surface = ColorsPalette.Neutral100,
    onSurface = ColorsPalette.Neutral50,
)

internal fun gravatarDarkColorScheme() = darkColorScheme(
    primary = ColorsPalette.Primary50,
    onPrimary = ColorsPalette.Neutral100,
    secondary = ColorsPalette.Secondary100,
    onSecondary = ColorsPalette.Neutral50,
    tertiary = ColorsPalette.Neutral90,
    onTertiary = ColorsPalette.Neutral100,
    error = ColorsPalette.Error50,
    onError = ColorsPalette.Error70,
    errorContainer = ColorsPalette.Error70,
    onErrorContainer = ColorsPalette.Secondary50,
    surfaceDim = ColorsPalette.Neutral70,
    surface = ColorsPalette.Neutral50,
    onSurface = ColorsPalette.Secondary100,
)
