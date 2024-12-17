package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class CtaSectionTest : RoborazziTest() {
    @Test
    fun ctaSectionLight() = gravatarScreenshotTest {
        CtaSection(
            title = "Oooops",
            message = "Something went wrong and we couldn't connect to Gravatar servers.",
            buttonText = "Retry",
            onButtonClick = {},
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun ctaSectionDark() = gravatarScreenshotTest {
        CtaSection(
            title = "Oooops",
            message = "Something went wrong and we couldn't connect to Gravatar servers.",
            buttonText = "Retry",
            onButtonClick = {},
        )
    }

    @Test
    fun ctaSectionNoTitleLight() = gravatarScreenshotTest {
        CtaSection(
            message = "Manage your profile for the web in one place.",
            buttonText = "Continue",
            onButtonClick = {},
        )
    }
}
