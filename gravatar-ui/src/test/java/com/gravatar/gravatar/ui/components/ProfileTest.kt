package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.Profile
import com.gravatar.ui.components.UserProfileState
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { Profile(UserProfileState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { Profile(UserProfileState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { Profile(UserProfileState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { Profile(UserProfileState.Loaded(completeProfile)) }
}
