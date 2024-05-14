package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.ProfileSummary
import com.gravatar.ui.components.UserProfileState
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileSummaryTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { ProfileSummary(UserProfileState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { ProfileSummary(UserProfileState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { ProfileSummary(UserProfileState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { ProfileSummary(UserProfileState.Loaded(completeProfile)) }
}
