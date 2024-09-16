package com.gravatar.gravatar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.gravatar.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfile
import com.gravatar.ui.gravatarTheme
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest {
        LargeProfile(
            ComponentState.Loading as ComponentState<Profile>,
        )
    }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest {
        LargeProfile(
            ComponentState.Loading as ComponentState<Profile>,
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfile(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfile(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileWithoutDescription() = gravatarScreenshotTest {
        LargeProfile(ComponentState.Loaded(completeProfile(description = "")))
    }

    @Test
    fun profileWithCustomComponents() = gravatarScreenshotTest {
        LargeProfile(
            state = ComponentState.Loaded(completeProfile()),
            avatar = { _ ->
                Image(painter = painterResource(id = R.drawable.gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }
}
