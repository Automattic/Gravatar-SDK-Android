package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class ErrorSectionTest : RoborazziTest() {
    @Test
    fun errorSectionLight() = gravatarScreenshotTest {
        ErrorSection(
            title = "Oooops",
            message = "Something went wrong and we couldn't connect to Gravatar servers.",
            buttonText = "Retry",
            onButtonClick = {},
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun errorSectionDark() = gravatarScreenshotTest {
        ErrorSection(
            title = "Oooops",
            message = "Something went wrong and we couldn't connect to Gravatar servers.",
            buttonText = "Retry",
            onButtonClick = {},
        )
    }
}
