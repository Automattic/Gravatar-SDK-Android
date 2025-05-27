package com.gravatar.quickeditor.ui.editor

import androidx.compose.runtime.Composable
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.ui.abouteditor.AboutEditor
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorUiState
import com.gravatar.quickeditor.ui.abouteditor.aboutFields
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPickerUiState
import com.gravatar.quickeditor.ui.avatarpicker.EmailAvatars
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.net.URI

class QuickEditorTest : RoborazziTest() {
    private val profile = defaultProfile(
        hash = "hash",
        displayName = "Henry Wong",
        location = "London, UK",
    )
    private val uiState = QuickEditorUiState(
        email = Email("william.henry.harrison@example.com"),
        profile = ComponentState.Loaded(profile),
        page = QuickEditorPage.AvatarPicker,
        pageNavigationEnabled = false,
    )

    @Test
    fun quickEditorWithAvatarPicker() = gravatarScreenshotTest {
        QuickEditor(
            uiState = uiState,
            onDoneClicked = { },
            onEditAvatarClicked = { },
            onEditAboutClicked = { },
        ) {
            AvatarPickerComponent()
        }
    }

    @Test
    fun quickEditorWithAvatarPickerWithNavigationEnabled() {
        gravatarScreenshotTest {
            QuickEditor(
                uiState = uiState.copy(pageNavigationEnabled = true),
                onDoneClicked = { },
                onEditAvatarClicked = { },
                onEditAboutClicked = { },
            ) {
                AvatarPickerComponent()
            }
        }
    }

    @Test
    fun quickEditorWithAboutEditor() = gravatarScreenshotTest {
        QuickEditor(
            uiState = uiState,
            onDoneClicked = { },
            onEditAvatarClicked = { },
            onEditAboutClicked = { },
        ) {
            AboutEditorComponent()
        }
    }

    @Test
    @Config(qualifiers = "+land")
    fun quickEditorWithAboutEditorLandscape() = gravatarScreenshotTest {
        QuickEditor(
            uiState = uiState.copy(compactWindow = true),
            onDoneClicked = { },
            onEditAvatarClicked = { },
            onEditAboutClicked = { },
        ) {
            AboutEditorComponent(compactWindow = true)
        }
    }

    @Composable
    private fun AvatarPickerComponent() {
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

    @Composable
    private fun AboutEditorComponent(compactWindow: Boolean = false) {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = profile.aboutFields(AboutInputField.all),
                isLoading = false,
                error = null,
                compactWindow = compactWindow,
            ),
            onEvent = { },
        )
    }
}
