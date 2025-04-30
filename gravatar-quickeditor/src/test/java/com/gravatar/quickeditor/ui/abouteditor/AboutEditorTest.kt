package com.gravatar.quickeditor.ui.abouteditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class AboutEditorTest : RoborazziTest() {

    private val aboutFields = AboutFields(
        personal = PersonalFields(
            aboutMe = AboutInputField.Personal.AboutMe(value = "My description"),
            displayName = AboutInputField.Personal.DisplayName(value = "John Doe"),
            location = AboutInputField.Personal.Location(value = "San Francisco, CA"),
            pronunciation = AboutInputField.Personal.Pronunciation(value = "John Doe"),
            pronouns = AboutInputField.Personal.Pronouns(value = "he/him"),
        ),
        professional = ProfessionalFields(
            company = AboutInputField.Professional.Company(value = "Automattic"),
            jobTitle = AboutInputField.Professional.JobTitle(value = "Software Engineer"),
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
