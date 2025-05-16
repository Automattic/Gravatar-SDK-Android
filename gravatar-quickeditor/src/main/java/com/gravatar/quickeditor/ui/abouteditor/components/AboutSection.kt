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
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorField
import com.gravatar.quickeditor.ui.abouteditor.AboutFields
import com.gravatar.quickeditor.ui.abouteditor.PersonalFields
import com.gravatar.quickeditor.ui.abouteditor.ProfessionalFields
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AboutSection(
    aboutFields: AboutFields,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (aboutFields.personal.anyVisible) {
            PersonalSection(
                personalFields = aboutFields.personal,
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        if (aboutFields.professional.anyVisible) {
            Spacer(modifier = Modifier.height(16.dp))
            ProfessionalSection(
                professionalFields = aboutFields.professional,
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

internal val AboutEditorField.labelRes: Int
    @StringRes get() = when (this) {
        is AboutEditorField.Personal.AboutMe -> R.string.gravatar_qe_about_field_label_about_me
        is AboutEditorField.Personal.DisplayName -> R.string.gravatar_qe_about_field_label_display_name
        is AboutEditorField.Personal.Location -> R.string.gravatar_qe_about_field_label_location
        is AboutEditorField.Personal.Pronouns -> R.string.gravatar_qe_about_field_label_pronouns
        is AboutEditorField.Personal.Pronunciation -> R.string.gravatar_qe_about_field_label_pronunciation
        is AboutEditorField.Professional.Company -> R.string.gravatar_qe_about_field_label_company
        is AboutEditorField.Professional.JobTitle -> R.string.gravatar_qe_about_field_label_job_title
    }

internal val AboutEditorField.descriptionRes: Int?
    @StringRes get() = when (this) {
        is AboutEditorField.Personal.AboutMe -> R.string.gravatar_qe_about_field_description_about_me
        is AboutEditorField.Personal.Pronunciation -> R.string.gravatar_qe_about_field_description_pronunciation
        is AboutEditorField.Personal.DisplayName,
        is AboutEditorField.Personal.Location,
        is AboutEditorField.Personal.Pronouns,
        is AboutEditorField.Professional.Company,
        is AboutEditorField.Professional.JobTitle,
        -> null
    }

internal fun AboutEditorField.copy(value: String): AboutEditorField {
    return when (this) {
        is AboutEditorField.Personal.AboutMe -> AboutEditorField.Personal.AboutMe(value)
        is AboutEditorField.Personal.DisplayName -> AboutEditorField.Personal.DisplayName(value)
        is AboutEditorField.Personal.Location -> AboutEditorField.Personal.Location(value)
        is AboutEditorField.Personal.Pronouns -> AboutEditorField.Personal.Pronouns(value)
        is AboutEditorField.Personal.Pronunciation -> AboutEditorField.Personal.Pronunciation(value)
        is AboutEditorField.Professional.Company -> AboutEditorField.Professional.Company(value)
        is AboutEditorField.Professional.JobTitle -> AboutEditorField.Professional.JobTitle(value)
    }
}

private val PersonalFields.anyVisible: Boolean
    get() = displayName.visible || aboutMe.visible || location.visible ||
        pronouns.visible || pronunciation.visible

private val ProfessionalFields.anyVisible: Boolean
    get() = jobTitle.visible || company.visible

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutSection(
                aboutFields = AboutFields(
                    personal = PersonalFields(
                        aboutMe = AboutEditorField.Personal.AboutMe(value = "My description"),
                        displayName = AboutEditorField.Personal.DisplayName(value = "John Doe"),
                        pronunciation = AboutEditorField.Personal.Pronunciation(value = "John Doe"),
                        pronouns = AboutEditorField.Personal.Pronouns(value = "he/him"),
                        location = AboutEditorField.Personal.Location(value = "San Francisco, CA"),
                    ),
                    professional = ProfessionalFields(
                        company = AboutEditorField.Professional.Company(value = "Automattic"),
                        jobTitle = AboutEditorField.Professional.JobTitle(value = "Software Engineer"),
                    ),
                ),
                formEnabled = true,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPersonalOnlyPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutSection(
                aboutFields = AboutFields(
                    personal = PersonalFields(
                        aboutMe = AboutEditorField.Personal.AboutMe(value = "My description"),
                        displayName = AboutEditorField.Personal.DisplayName(value = "John Doe"),
                        pronunciation = AboutEditorField.Personal.Pronunciation(value = "John Doe"),
                        pronouns = AboutEditorField.Personal.Pronouns(value = "he/him"),
                        location = AboutEditorField.Personal.Location(value = "San Francisco, CA"),
                    ),
                    professional = ProfessionalFields(
                        company = AboutEditorField.Professional.Company(value = "Automattic", visible = false),
                        jobTitle = AboutEditorField.Professional.JobTitle(value = "Software Engineer", visible = false),
                    ),
                ),
                formEnabled = true,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
