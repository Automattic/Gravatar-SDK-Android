package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class PopupButtonTest : RoborazziTest() {
    @Test
    fun popupButtonLightMode() {
        gravatarScreenshotTest {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
            ) {
                PopupButton(
                    text = "Choose a Photo",
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    contentDescription = "Content description",
                    iconRes = com.gravatar.quickeditor.R.drawable.photo_library,
                    onClick = {},
                )
            }
        }
    }

    @Test
    fun popupButtonEllipsizedText() {
        gravatarScreenshotTest {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
            ) {
                PopupButton(
                    text = "Choose a Photo",
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    contentDescription = "Content description",
                    iconRes = com.gravatar.quickeditor.R.drawable.photo_library,
                    onClick = {},
                    modifier = Modifier.width(120.dp),
                )
            }
        }
    }

    @Test
    @Config(qualifiers = "+night")
    fun popupButtonDarkMode() {
        gravatarScreenshotTest {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
            ) {
                PopupButton(
                    text = "Choose a Photo",
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    contentDescription = "Content description",
                    iconRes = com.gravatar.quickeditor.R.drawable.photo_library,
                    onClick = {},
                )
            }
        }
    }
}
