package com.gravatar.quickeditor.ui.abouteditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.avatarpicker.SectionError
import com.gravatar.quickeditor.ui.editor.AboutInputField
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class AboutEditorTest : RoborazziTest() {
    private val aboutFields = setOf(
        AboutEditorField(
            type = AboutInputField.DisplayName,
            value = "John Doe",
            maxLines = 1,
        ),
        AboutEditorField(
            type = AboutInputField.AboutMe,
            value = "My description",
            maxLines = 3,
        ),
        AboutEditorField(
            type = AboutInputField.Pronunciation,
            value = "John Doe",
        ),
        AboutEditorField(
            type = AboutInputField.Pronouns,
            value = "he/him",
        ),
        AboutEditorField(
            type = AboutInputField.Location,
            value = "San Francisco, CA",
        ),
        AboutEditorField(
            type = AboutInputField.JobTitle,
            value = "Software Engineer",
        ),
        AboutEditorField(
            type = AboutInputField.Company,
            value = "Automattic",
        ),
        AboutEditorField(
            type = AboutInputField.FirstName,
            value = "John",
        ),
        AboutEditorField(
            type = AboutInputField.LastName,
            value = "Doe",
        ),
    )

    @Test
    fun aboutEditorLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
            ),
            onEvent = { },
        )
    }

    @Test
    @Config(qualifiers = "+land")
    fun aboutEditorLoadedLandscape() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
                compactWindow = true,
            ),
            onEvent = { },
        )
    }

    @Test
    fun aboutEditorPersonalLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields.filter { it.type.isPersonal }.toSet(),
                isLoading = false,
            ),
            onEvent = { },
        )
    }

    @Test
    fun aboutEditorExtrasLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields.filter { it.type.isExtra }.toSet(),
                isLoading = false,
            ),
            onEvent = { },
        )
    }

    @Test
    fun aboutEditorExtrasAndOtherSectionLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields.filter { it.type.isExtra || it.type == AboutInputField.DisplayName }.toSet(),
                isLoading = false,
            ),
            onEvent = { },
        )
    }

    @Config(qualifiers = "+night")
    @Test
    fun aboutEditorLoadedDark() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
            ),
            onEvent = { },
        )
    }

    @Test
    fun heightRestrictedAboutEditorLoaded() = gravatarScreenshotTest {
        Box(modifier = Modifier.height(400.dp)) {
            AboutEditor(
                uiState = AboutEditorUiState(
                    aboutFields = aboutFields,
                    isLoading = false,
                ),
                onEvent = { },
            )
        }
    }

    @Test
    fun aboutEditorLoading() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                isLoading = true,
            ),
            onEvent = { },
        )
    }

    @Test
    fun aboutEditorSaving() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
                savingProfile = true,
            ),
            onEvent = { },
        )
    }

    @Test
    fun aboutEditorNoInternetError() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
                savingProfile = true,
                error = SectionError.NoInternetConnection,
            ),
            onEvent = { },
        )
    }
}
