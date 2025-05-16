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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorField
import com.gravatar.quickeditor.ui.editor.AboutInputField
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AboutSection(
    aboutFields: Set<AboutEditorField>,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (aboutFields.any { it.type.isPersonal }) {
            AboutFieldsSection(
                label = stringResource(R.string.gravatar_qe_about_field_section_label_personal),
                fields = aboutFields.filter { it.type.isPersonal }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        if (aboutFields.any { it.type.isProfessional }) {
            Spacer(modifier = Modifier.height(16.dp))
            AboutFieldsSection(
                label = stringResource(R.string.gravatar_qe_about_field_section_label_professional),
                fields = aboutFields.filter { it.type.isProfessional }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

internal val AboutEditorField.labelRes: Int
    @StringRes get() = when (this.type) {
        AboutInputField.AboutMe -> R.string.gravatar_qe_about_field_label_about_me
        AboutInputField.DisplayName -> R.string.gravatar_qe_about_field_label_display_name
        AboutInputField.Location -> R.string.gravatar_qe_about_field_label_location
        AboutInputField.Pronouns -> R.string.gravatar_qe_about_field_label_pronouns
        AboutInputField.Pronunciation -> R.string.gravatar_qe_about_field_label_pronunciation
        AboutInputField.Company -> R.string.gravatar_qe_about_field_label_company
        AboutInputField.JobTitle -> R.string.gravatar_qe_about_field_label_job_title
        else -> R.string.gravatar_qe_about_field_label_display_name
    }

internal val AboutEditorField.descriptionRes: Int?
    @StringRes get() = when (this.type) {
        AboutInputField.AboutMe -> R.string.gravatar_qe_about_field_description_about_me
        AboutInputField.Pronunciation -> R.string.gravatar_qe_about_field_description_pronunciation
        else -> null
    }

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutSection(
                aboutFields = setOf(
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
                        type = AboutInputField.Company,
                        value = "Automattic",
                    ),
                    AboutEditorField(
                        type = AboutInputField.JobTitle,
                        value = "Software Engineer",
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
                aboutFields = setOf(
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
                ),
                formEnabled = true,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
