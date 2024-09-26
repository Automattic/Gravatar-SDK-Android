package com.gravatar.uitestutils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Before
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

    @OptIn(ExperimentalRoborazziApi::class)
    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputDirectoryPath = SCREENSHOTS_PATH,
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(
                    imageComparator = SimpleImageComparator(maxDistance = 0.007F, hShift = 1),
                    changeThreshold = 0.01f,
                ),
            ),
        ),
    )

    @OptIn(ExperimentalCoilApi::class)
    @Before
    fun setUp() {
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorDrawable(Color.GRAY))
            .build()
        val imageLoader = ImageLoader.Builder(ApplicationProvider.getApplicationContext())
            .components { add(engine) }
            .build()
        Coil.setImageLoader(imageLoader)
    }

    fun screenshotTest(composable: @Composable () -> Unit) {
        captureRoboImage {
            composable()
        }
    }
}

/**
 * You can filter ScreenshotTests using -Pscreenshot parameter
 */
interface ScreenshotTests
