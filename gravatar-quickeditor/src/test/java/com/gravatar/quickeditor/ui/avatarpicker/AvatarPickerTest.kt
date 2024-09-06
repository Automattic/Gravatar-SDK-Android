package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.repository.IdentityAvatars
import com.gravatar.quickeditor.ui.editor.ContentLayout
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
                contentLayout = ContentLayout.Horizontal,
                identityAvatars = IdentityAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url1"
                            format = 0
                            imageId = "1"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = ""
                        },
                        Avatar {
                            imageUrl = "/image/url2"
                            format = 0
                            imageId = "2"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
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
                contentLayout = ContentLayout.Horizontal,
                identityAvatars = IdentityAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url1"
                            format = 0
                            imageId = "1"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = ""
                        },
                        Avatar {
                            imageUrl = "/image/url2"
                            format = 0
                            imageId = "2"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
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
