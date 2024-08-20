package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class QETopBarTest : RoborazziTest() {
    @Test
    fun qrTopBarLight() = gravatarScreenshotTest {
        QETopBar(onDoneClick = {})
    }

    @Test
    @Config(qualifiers = "+night")
    fun qrTopBarDark() = gravatarScreenshotTest {
        QETopBar(onDoneClick = {})
    }
}
