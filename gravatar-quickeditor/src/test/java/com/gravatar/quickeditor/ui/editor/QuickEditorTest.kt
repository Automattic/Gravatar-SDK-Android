package com.gravatar.quickeditor.ui.editor

import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPickerUiState
import com.gravatar.quickeditor.ui.avatarpicker.EmailAvatars
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import java.net.URI

class QuickEditorTest : RoborazziTest() {
    private val profile = defaultProfile(
        hash = "hash",
        displayName = "Henry Wong",
        location = "London, UK",
    )

    @Test
    fun quickEditorWithAvatarPicker() = gravatarScreenshotTest {
        QuickEditor(
            uiState = QuickEditorUiState(
                email = Email("william.henry.harrison@example.com"),
                profile = ComponentState.Loaded(profile),
            ),
            onDoneClicked = { },
        ) {
            AvatarPicker(
                uiState = AvatarPickerUiState(
                    email = Email("william.henry.harrison@example.com"),
                    avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                    emailAvatars = EmailAvatars(
                        avatars = listOf(
                            Avatar {
                                imageUrl = URI.create("https://gravatar.com/avatar/test")
                                imageId = "1"
                                rating = Avatar.Rating.G
                                altText = "alt"
                                updatedDate = ""
                            },
                            Avatar {
                                imageUrl = URI.create("https://gravatar.com/avatar/test2")
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
}
