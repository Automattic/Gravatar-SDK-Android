package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test

class QESectionMessageTest : RoborazziTest() {
    @Test
    fun qeTextTestNoOrphan() = gravatarScreenshotTest {
        Surface(
            modifier = Modifier.width(220.dp),
        ) {
            QESectionMessage(message = "This is a long text that should not break just before the last word.")
        }
    }
}
