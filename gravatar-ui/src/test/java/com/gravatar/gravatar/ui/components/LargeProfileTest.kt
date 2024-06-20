package com.gravatar.gravatar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfile
import com.gravatar.ui.gravatarTheme
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { LargeProfile(ComponentState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { LargeProfile(ComponentState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfile(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfile(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileWithCustomComponents() = gravatarScreenshotTest {
        LargeProfile(
            state = ComponentState.Loaded(completeProfile),
            avatar = { _ ->
                Image(painter = painterResource(id = R.drawable.gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }
}
