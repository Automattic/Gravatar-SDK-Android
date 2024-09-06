package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class AvatarsSectionTest : RoborazziTest() {
    @Test
    fun avatarPickerListLoaded() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = createAvatarList(2),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Horizontal,
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
                avatars = createAvatarList(2),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Horizontal,
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
                contentLayout = ContentLayout.Horizontal,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }

    @Test
    fun avatarPickerButtonDisabled() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = createAvatarList(2),
                scrollToIndex = null,
                uploadButtonEnabled = false,
                contentLayout = ContentLayout.Horizontal,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }

    @Test
    fun avatarPickerVertical() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = createAvatarList(5),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Vertical,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = {},
        )
    }
}

private fun createAvatarList(size: Int): List<AvatarUi> {
    return List(size) {
        AvatarUi.Uploaded(
            avatar = Avatar {
                imageUrl = "/image/url"
                format = 0
                imageId = it.toString()
                rating = "G"
                altText = "alt"
                isCropped = true
                updatedDate = ""
            },
            isLoading = false,
            isSelected = it == 0,
        )
    }
}
