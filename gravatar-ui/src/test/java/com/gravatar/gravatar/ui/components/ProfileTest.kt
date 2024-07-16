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
import com.gravatar.api.models.Profile as LegacyProfile

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
                Image(painter = painterResource(id = R.drawable.gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }

    // Compatibility tests using old models - We should remove these tests once we remove the old models
    @Test
    fun legacyProfileLoadingLight() = gravatarScreenshotTest {
        Profile(ComponentState.Loading as ComponentState<LegacyProfile>)
    }

    @Test
    fun legacyProfileLight() = gravatarScreenshotTest { Profile(ComponentState.Loaded(completeProfile)) }
    // END - Compatibility tests using old models - We should remove these tests once we remove the old models
}
