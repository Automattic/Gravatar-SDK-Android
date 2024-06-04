package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.Profile
import org.junit.Test
import org.robolectric.annotation.Config

class ProfileTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { Profile(ComponentState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { Profile(ComponentState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { Profile(ComponentState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { Profile(ComponentState.Loaded(completeProfile)) }
}
