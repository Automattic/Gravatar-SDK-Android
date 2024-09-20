package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.repository.EmailAvatars
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class AvatarPickerTest : RoborazziTest() {
    private val profile = defaultProfile(
        hash = "hash",
        displayName = "Henry Wong",
        location = "London, UK",
    )

    @Test
    fun avatarPickerListLoaded() = gravatarScreenshotTest {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                profile = ComponentState.Loaded(profile),
                email = Email("william.henry.harrison@example.com"),
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                emailAvatars = EmailAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url1"
                            imageId = "1"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                        Avatar {
                            imageUrl = "/image/url2"
                            imageId = "2"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                    ),
                    selectedAvatarId = "1",
                ),
            ),
            onEvent = { },
        )
    }

    @Config(qualifiers = "+night")
    @Test
    fun avatarPickerListLoadedDark() = gravatarScreenshotTest {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                profile = ComponentState.Loaded(profile),
                email = Email("william.henry.harrison@example.com"),
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                emailAvatars = EmailAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url1"
                            imageId = "1"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                        Avatar {
                            imageUrl = "/image/url2"
                            imageId = "2"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                    ),
                    selectedAvatarId = "1",
                ),
            ),
            onEvent = { },
        )
    }
}
