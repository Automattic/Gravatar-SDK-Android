package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.ui.components.ComponentState
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileCardTest : RoborazziTest() {
    private val profile = defaultProfile(
        hash = "hash",
        displayName = "Henry Wong",
        location = "London, UK",
    )

    @Test
    fun profileCardLightMode() {
        gravatarScreenshotTest {
            ProfileCard(
                profile = ComponentState.Loaded(profile),
                modifier = Modifier.padding(20.dp),
            )
        }
    }

    @Test
    @Config(qualifiers = "+night")
    fun profileCardDarkMode() = gravatarScreenshotTest {
        ProfileCard(
            profile = ComponentState.Loaded(profile),
            modifier = Modifier.padding(20.dp),
        )
    }
}
