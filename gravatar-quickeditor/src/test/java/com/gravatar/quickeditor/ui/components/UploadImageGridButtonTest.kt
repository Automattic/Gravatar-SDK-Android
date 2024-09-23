package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test

class UploadImageGridButtonTest : RoborazziTest() {
    @Test
    fun uploadImageGridButtonEnabled() = gravatarScreenshotTest {
        Surface {
            UploadImageGridButton(
                onClick = {},
                enabled = true,
                modifier = Modifier.size(avatarSize),
            )
        }
    }

    @Test
    fun uploadImageGridButtonDisabled() = gravatarScreenshotTest {
        Surface {
            UploadImageGridButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.size(avatarSize),
            )
        }
    }
}
