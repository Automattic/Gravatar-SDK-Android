package com.gravatar.gravatar.ui.components

import com.gravatar.gravatar.ui.RoborazziTest
import com.gravatar.gravatar.ui.completeProfile
import com.gravatar.ui.components.LargeProfileSummary
import com.gravatar.ui.components.UserProfileState
import org.junit.Test
import org.robolectric.annotation.Config

class LargeProfileSummaryTest : RoborazziTest() {
    @Test
    @Config(qualifiers = "+night")
    fun profileLoadingDark() = gravatarScreenshotTest { LargeProfileSummary(UserProfileState.Loading) }

    @Test
    fun profileLoadingLight() = gravatarScreenshotTest { LargeProfileSummary(UserProfileState.Loading) }

    @Test
    @Config(qualifiers = "+night")
    fun profileDark() = gravatarScreenshotTest { LargeProfileSummary(UserProfileState.Loaded(completeProfile)) }

    @Test
    fun profileLight() = gravatarScreenshotTest { LargeProfileSummary(UserProfileState.Loaded(completeProfile)) }
}
