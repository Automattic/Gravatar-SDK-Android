package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.LargeProfile
import com.gravatar.ui.components.UserProfileState
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { LargeProfile(UserProfileState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { LargeProfile(UserProfileState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfile(UserProfileState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfile(UserProfileState.Loaded(completeProfile)) }
}
