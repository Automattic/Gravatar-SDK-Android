package com.gravatar.gravatar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.gravatar.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfileSummary
import com.gravatar.ui.gravatarTheme
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileSummaryTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest {
        LargeProfileSummary(ComponentState.Loading as ComponentState<Profile>)
    }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest {
        LargeProfileSummary(ComponentState.Loading as ComponentState<Profile>)
    }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loaded(completeProfile())) }

    @Test
    fun profileWithCustomComponents() = gravatarScreenshotTest {
        LargeProfileSummary(
            state = ComponentState.Loaded(completeProfile()),
            avatar = { _ ->
                Image(painter = painterResource(id = R.drawable.gravatar_gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }
}
