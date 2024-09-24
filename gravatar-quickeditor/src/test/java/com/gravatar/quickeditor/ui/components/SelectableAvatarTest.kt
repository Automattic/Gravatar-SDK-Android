package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test

class SelectableAvatarTest : RoborazziTest() {
    @Test
    fun selectableAvatarSelected() = gravatarScreenshotTest {
        SelectableAvatar(
            "https://fakeavatarurl.com/hash",
            isSelected = true,
            loadingState = AvatarLoadingState.None,
            onAvatarClicked = {},
            modifier = Modifier.size(150.dp),
        )
    }

    @Test
    fun selectableAvatarNotSelected() = gravatarScreenshotTest {
        SelectableAvatar(
            "https://fakeavatarurl.com/hash",
            isSelected = false,
            loadingState = AvatarLoadingState.None,
            onAvatarClicked = {},
            modifier = Modifier.size(150.dp),
        )
    }

    @Test
    fun selectableAvatarLoading() = gravatarScreenshotTest {
        SelectableAvatar(
            "https://fakeavatarurl.com/hash",
            isSelected = false,
            loadingState = AvatarLoadingState.Loading,
            onAvatarClicked = {},
            modifier = Modifier.size(150.dp),
        )
    }

    @Test
    fun selectableAvatarFailure() = gravatarScreenshotTest {
        SelectableAvatar(
            "https://fakeavatarurl.com/hash",
            isSelected = false,
            loadingState = AvatarLoadingState.Failure,
            onAvatarClicked = {},
            modifier = Modifier.size(150.dp),
        )
    }
}
