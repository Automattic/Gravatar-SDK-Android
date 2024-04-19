package com.gravatar.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * [GravatarTheme] is a composable that wraps the content of the application with the Gravatar theme.
 */
@Composable
public fun GravatarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = gravatarTheme.colorScheme,
        typography = gravatarTheme.typography,
        shapes = gravatarTheme.shapes,
    ) {
        content()
    }
}

/**
 * [GravatarTheme] contains the colors, typography, and shapes to be used in the Gravatar UI components.
 * Those values follow the Gravatar style guide but can be customized by the user.
 * In order to customize the theme, the user can provide a custom [GravatarTheme] using [Composition Local](https://developer.android.com/develop/ui/compose/compositionlocal)
 *
 * [colorScheme] The color scheme to be used in the Gravatar UI components
 * [typography] The typography to be used in the Gravatar UI components
 * [shapes] The shapes to be used in the Gravatar UI components
 */
public interface GravatarTheme {
    public val colorScheme: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme

    public val typography: Typography
        @Composable
        get() = MaterialTheme.typography

    public val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes
}

/**
 * [LocalGravatarTheme] is a CompositionLocal that provides the current [GravatarTheme].
 */
public val LocalGravatarTheme: ProvidableCompositionLocal<GravatarTheme> =
    staticCompositionLocalOf { object : GravatarTheme {} }

/** The current [GravatarTheme]. */
public val gravatarTheme: GravatarTheme
    @Composable
    get() = LocalGravatarTheme.current
