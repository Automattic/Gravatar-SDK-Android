package com.gravatar.quickeditor.ui.abouteditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    )

    @Test
    fun aboutEditorLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields,
                isLoading = false,
            ),
            onSaveClick = { },
            onValueChange = { },
        )
    }

    @Test
    fun aboutEditorPersonalLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = aboutFields.filter { it.type.isPersonal }.toSet(),
                isLoading = false,
            ),
            onSaveClick = { },
            onValueChange = { },
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
            onSaveClick = { },
            onValueChange = { },
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
                onSaveClick = { },
                onValueChange = { },
            )
        }
    }

    @Test
    fun aboutEditorLoading() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                isLoading = true,
            ),
            onSaveClick = { },
            onValueChange = { },
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
            onSaveClick = { },
            onValueChange = { },
        )
    }
}
