package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (aboutFields.any { it.type.isPersonal }) {
            AboutFieldsSection(
                label = aboutFields.personalLabelRes?.let { stringResource(it) },
                fields = aboutFields.filter { it.type.isPersonal }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
                modifier = Modifier,
            )
        }
        if (aboutFields.any { it.type.isProfessional }) {
            AboutFieldsSection(
                label = aboutFields.professionalLabelRes?.let { stringResource(it) },
                fields = aboutFields.filter { it.type.isProfessional }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        if (aboutFields.any { it.type.isExtra }) {
            AboutFieldsSection(
                label = stringResource(R.string.gravatar_qe_about_field_section_label_extras),
                sectionDescription = stringResource(R.string.gravatar_qe_about_field_section_description_extras),
                fields = aboutFields.filter { it.type.isExtra }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
    }
}

private val Set<AboutEditorField>.personalLabelRes: Int?
    @StringRes get() = if (hasOtherSectionsThan(Section.PERSONAL)) {
        R.string.gravatar_qe_about_field_section_label_personal
    } else {
        null
    }

private val Set<AboutEditorField>.professionalLabelRes: Int?
    @StringRes get() = if (hasOtherSectionsThan(Section.PROFESSIONAL)) {
        R.string.gravatar_qe_about_field_section_label_professional
    } else {
        null
    }

private fun Set<AboutEditorField>.hasOtherSectionsThan(other: Section): Boolean {
    return any { it.type.section != other }
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
        AboutInputField.FirstName -> R.string.gravatar_qe_about_field_label_first_name
        AboutInputField.LastName -> R.string.gravatar_qe_about_field_label_last_name
        else -> R.string.gravatar_qe_about_field_label_display_name
    }

internal val AboutEditorField.descriptionRes: Int?
    @StringRes get() = when (this.type) {
        AboutInputField.AboutMe -> R.string.gravatar_qe_about_field_description_about_me
        AboutInputField.Pronunciation -> R.string.gravatar_qe_about_field_description_pronunciation
        else -> null
    }

private enum class Section {
    PERSONAL,
    PROFESSIONAL,
    EXTRA,
}

private val AboutInputField.section: Section
    get() = when {
        isPersonal -> Section.PERSONAL
        isProfessional -> Section.PROFESSIONAL
        isExtra -> Section.EXTRA
        else -> Section.PERSONAL
    }

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    GravatarTheme {
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
                AboutEditorField(
                    type = AboutInputField.FirstName,
                    value = "John",
                ),
                AboutEditorField(
                    type = AboutInputField.LastName,
                    value = "Doe",
                ),
            ),
            formEnabled = true,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPersonalOnlyPreview() {
    GravatarTheme {
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

@Preview(showBackground = true)
@Composable
internal fun AboutSectionExtrasOnlyPreview() {
    GravatarTheme {
        AboutSection(
            aboutFields = setOf(
                AboutEditorField(
                    type = AboutInputField.FirstName,
                    value = "John",
                ),
                AboutEditorField(
                    type = AboutInputField.LastName,
                    value = "Doe",
                ),
            ),
            formEnabled = true,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
