package com.gravatar.quickeditor.ui.avatarpicker

import androidx.compose.material3.Surface
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import java.time.Instant

class AvatarPickerTest : RoborazziTest() {
    @Test
    fun avatarPickerListLoaded() = gravatarScreenshotTest {
        Surface {
            AvatarPicker(
                uiState = AvatarPickerUiState(
                    email = Email("william.henry.harrison@example.com"),
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url1"
                            format = 0
                            imageId = "1"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = Instant.now()
                        },
                        Avatar {
                            imageUrl = "/image/url2"
                            format = 0
                            imageId = "2"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = Instant.now()
                        },
                    ),
                ),
                onAvatarSelected = {},
            )
        }
    }
}
