package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.ProfileSummary
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileSummaryTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { ProfileSummary(ComponentState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { ProfileSummary(ComponentState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { ProfileSummary(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { ProfileSummary(ComponentState.Loaded(completeProfile)) }
}
