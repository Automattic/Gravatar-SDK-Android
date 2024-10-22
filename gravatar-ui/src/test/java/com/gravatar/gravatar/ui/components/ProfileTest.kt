package com.gravatar.gravatar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.gravatar.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.Profile
import com.gravatar.ui.gravatarTheme
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { Profile(ComponentState.Loading as ComponentState<Profile>) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { Profile(ComponentState.Loading as ComponentState<Profile>) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { Profile(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileLight() = gravatarScreenshotTest { Profile(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileWithEmptyDescription() = gravatarScreenshotTest {
        Profile(ComponentState.Loaded(completeProfile(description = "")))
    }

    @Test
    fun profileWithCustomComponents() = gravatarScreenshotTest {
        Profile(
            state = ComponentState.Loaded(completeProfile()),
            avatar = { _ ->
                Image(painter = painterResource(id = R.drawable.gravatar_gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }
}
