package com.gravatar.gravatar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfileSummary
import com.gravatar.ui.gravatarTheme
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileSummaryTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfileSummary(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileWithCustomComponents() = gravatarScreenshotTest {
        LargeProfileSummary(
            state = ComponentState.Loaded(completeProfile),
            avatar = { _ ->
                Image(painter = painterResource(id = R.drawable.gravatar_icon), contentDescription = "")
            },
            viewProfile = { _ -> Text(text = "Custom Profile Button", color = gravatarTheme.colorScheme.primary) },
        )
    }
}
