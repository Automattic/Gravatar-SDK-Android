package com.gravatar.quickeditor.ui.alttext

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.ui.GravatarTheme
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.net.URI

class AltTextPageTest : RoborazziTest() {
    @Test
    fun altTextPageLoaded() = gravatarScreenshotTest {
        GravatarTheme {
            AltTextPage(
                altTextState = AltTextUiState(
                    avatarUrl = URI("https://gravatar.com/avatar/test"),
                    isUpdating = false,
                    altText = "alt",
                    altTextMaxLength = 125,
                ),
                onEvent = { },
            )
        }
    }

    @Config(qualifiers = "+night")
    @Test
    fun altTextPageLoadedDark() = gravatarScreenshotTest {
        GravatarTheme {
            AltTextPage(
                altTextState = AltTextUiState(
                    avatarUrl = URI("https://gravatar.com/avatar/test"),
                    isUpdating = false,
                    altText = "alt",
                    altTextMaxLength = 125,
                ),
                onEvent = { },
            )
        }
    }

    @Test
    fun emptyAltTextPageLoaded() = gravatarScreenshotTest {
        GravatarTheme {
            AltTextPage(
                altTextState = AltTextUiState(
                    avatarUrl = URI("https://gravatar.com/avatar/test"),
                    isUpdating = false,
                    altText = "New alt text",
                    altTextMaxLength = 125,
                ),
                onEvent = { },
            )
        }
    }

    @Config(qualifiers = "+night")
    @Test
    fun emptyAltTextPageLoadedDark() = gravatarScreenshotTest {
        GravatarTheme {
            AltTextPage(
                altTextState = AltTextUiState(
                    avatarUrl = URI("https://gravatar.com/avatar/test"),
                    isUpdating = false,
                    altText = "New alt text",
                    altTextMaxLength = 125,
                ),
                onEvent = { },
            )
        }
    }
}
