package com.gravatar.gravatar.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.gravatar.ui.GravatarTheme
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
@Category(ScreenshotTests::class)
abstract class RoborazziTest {
    private companion object {
        const val SCREENSHOTS_PATH = "screenshotTests/roborazzi"
    }

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputDirectoryPath = SCREENSHOTS_PATH,
        ),
    )

    fun gravatarScreenshotTest(composable: @Composable () -> Unit) {
        captureRoboImage {
            GravatarTheme {
                Surface {
                    composable()
                }
            }
        }
    }
}

/**
 * You can filter ScreenshotTests using -Pscreenshot parameter
 */
interface ScreenshotTests
