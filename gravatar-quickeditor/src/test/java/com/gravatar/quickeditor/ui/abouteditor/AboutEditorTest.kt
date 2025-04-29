package com.gravatar.quickeditor.ui.abouteditor

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test

class AboutEditorTest : RoborazziTest() {
    @Test
    fun aboutEditorLoaded() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                aboutFields = AboutFields(
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
                ),
                isLoading = false,
            ),
            onValueChange = { },
        )
    }

    @Test
    fun aboutEditorLoading() = gravatarScreenshotTest {
        AboutEditor(
            uiState = AboutEditorUiState(
                isLoading = true,
            ),
            onValueChange = { },
        )
    }
}
