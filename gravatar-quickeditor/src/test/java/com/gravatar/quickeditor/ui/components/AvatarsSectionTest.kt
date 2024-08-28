package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.time.Instant

class AvatarsSectionTest : RoborazziTest() {
    @Test
    fun avatarPickerListLoaded() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = avatars,
                scrollToIndex = null,
                uploadButtonEnabled = true,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun avatarPickerListLoadedDark() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = avatars,
                scrollToIndex = null,
                uploadButtonEnabled = true,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }

    @Test
    fun avatarPickerListEmpty() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = emptyList(),
                scrollToIndex = null,
                uploadButtonEnabled = true,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }

    @Test
    fun avatarPickerButtonDisabled() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = avatars,
                scrollToIndex = null,
                uploadButtonEnabled = false,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }
}

private val avatars = listOf(
    AvatarUi.Uploaded(
        avatar = Avatar {
            imageUrl = "/image/url"
            format = 0
            imageId = "1"
            rating = "G"
            altText = "alt"
            isCropped = true
            updatedDate = Instant.now()
        },
        isLoading = false,
        isSelected = true,
    ),
    AvatarUi.Uploaded(
        avatar = Avatar {
            imageUrl = "/image/url"
            format = 0
            imageId = "2"
            rating = "G"
            altText = "alt"
            isCropped = true
            updatedDate = Instant.now()
        },
        isLoading = false,
        isSelected = true,
    ),
)
