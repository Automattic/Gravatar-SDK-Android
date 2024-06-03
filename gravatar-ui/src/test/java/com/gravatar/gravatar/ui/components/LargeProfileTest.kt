package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfile
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
}
