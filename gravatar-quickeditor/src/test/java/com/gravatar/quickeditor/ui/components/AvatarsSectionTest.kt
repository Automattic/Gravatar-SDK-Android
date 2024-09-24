package com.gravatar.quickeditor.ui.components

import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.net.URI

class AvatarsSectionTest : RoborazziTest() {
    @Test
    fun avatarPickerListLoaded() = gravatarScreenshotTest {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = createAvatarList(2),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
            ),
            onLocalImageSelected = { },
            onAvatarSelected = { },
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Vertical,
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
                imageUrl = URI.create("https://gravatar.com/avatar/test/$it")
                imageId = it.toString()
                rating = Avatar.Rating.G
                altText = "alt"
                updatedDate = ""
            },
            isLoading = false,
            isSelected = it == 0,
        )
    }
}
