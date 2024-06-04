package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LargeProfileSummary
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
}
