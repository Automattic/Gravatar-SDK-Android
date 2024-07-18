package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test

class SelectableAvatarTest : RoborazziTest() {
    @Test
    fun selectableAvatarSelected() = gravatarScreenshotTest {
        Surface {
            SelectableAvatar(
                "https://fakeavatarurl.com/hash",
                true,
                {},
                Modifier.size(150.dp),
            )
        }
    }

    @Test
    fun selectableAvatarNotSelected() = gravatarScreenshotTest {
        SelectableAvatar(
            "https://fakeavatarurl.com/hash",
            false,
            {},
            Modifier.size(150.dp),
        )
    }
}
