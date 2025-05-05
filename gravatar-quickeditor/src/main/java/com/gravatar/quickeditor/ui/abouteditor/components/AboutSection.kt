package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.abouteditor.AboutFields
import com.gravatar.quickeditor.ui.abouteditor.AboutInputField
import com.gravatar.quickeditor.ui.abouteditor.PersonalFields
import com.gravatar.quickeditor.ui.abouteditor.ProfessionalFields
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AboutSection(
    aboutFields: AboutFields,
    formEnabled: Boolean,
    onValueChange: (AboutInputField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PersonalSection(
            personalFields = aboutFields.personal,
            formEnabled = formEnabled,
            onValueChange = onValueChange,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfessionalSection(
            professionalFields = aboutFields.professional,
            formEnabled = formEnabled,
            onValueChange = onValueChange,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

internal val AboutInputField.labelRes: Int
    @StringRes get() = when (this) {
        is AboutInputField.Personal.AboutMe -> R.string.gravatar_qe_about_field_label_about_me
        is AboutInputField.Personal.DisplayName -> R.string.gravatar_qe_about_field_label_display_name
        is AboutInputField.Personal.Location -> R.string.gravatar_qe_about_field_label_location
        is AboutInputField.Personal.Pronouns -> R.string.gravatar_qe_about_field_label_pronouns
        is AboutInputField.Personal.Pronunciation -> R.string.gravatar_qe_about_field_label_pronunciation
        is AboutInputField.Professional.Company -> R.string.gravatar_qe_about_field_label_company
        is AboutInputField.Professional.JobTitle -> R.string.gravatar_qe_about_field_label_job_title
    }

internal val AboutInputField.descriptionRes: Int?
    @StringRes get() = when (this) {
        is AboutInputField.Personal.AboutMe -> R.string.gravatar_qe_about_field_description_about_me
        is AboutInputField.Personal.Pronunciation -> R.string.gravatar_qe_about_field_description_pronunciation
        is AboutInputField.Personal.DisplayName,
        is AboutInputField.Personal.Location,
        is AboutInputField.Personal.Pronouns,
        is AboutInputField.Professional.Company,
        is AboutInputField.Professional.JobTitle,
        -> null
    }

internal fun AboutInputField.copy(value: String): AboutInputField {
    return when (this) {
        is AboutInputField.Personal.AboutMe -> AboutInputField.Personal.AboutMe(value)
        is AboutInputField.Personal.DisplayName -> AboutInputField.Personal.DisplayName(value)
        is AboutInputField.Personal.Location -> AboutInputField.Personal.Location(value)
        is AboutInputField.Personal.Pronouns -> AboutInputField.Personal.Pronouns(value)
        is AboutInputField.Personal.Pronunciation -> AboutInputField.Personal.Pronunciation(value)
        is AboutInputField.Professional.Company -> AboutInputField.Professional.Company(value)
        is AboutInputField.Professional.JobTitle -> AboutInputField.Professional.JobTitle(value)
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutSection(
                aboutFields = AboutFields(
                    personal = PersonalFields(
                        aboutMe = AboutInputField.Personal.AboutMe(value = "My description"),
                        displayName = AboutInputField.Personal.DisplayName(value = "John Doe"),
                        pronunciation = AboutInputField.Personal.Pronunciation(value = "John Doe"),
                        pronouns = AboutInputField.Personal.Pronouns(value = "he/him"),
                        location = AboutInputField.Personal.Location(value = "San Francisco, CA"),
                    ),
                    professional = ProfessionalFields(
                        company = AboutInputField.Professional.Company(value = "Automattic"),
                        jobTitle = AboutInputField.Professional.JobTitle(value = "Software Engineer"),
                    ),
                ),
                formEnabled = true,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
