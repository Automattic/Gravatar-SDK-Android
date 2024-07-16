package com.gravatar.gravatar.ui

import androidx.compose.runtime.Composable
import com.gravatar.ui.GravatarTheme
import com.gravatar.uitestutils.RoborazziTest

internal fun RoborazziTest.gravatarScreenshotTest(composable: @Composable () -> Unit) {
    screenshotTest {
        GravatarTheme {
            composable()
        }
    }
}
