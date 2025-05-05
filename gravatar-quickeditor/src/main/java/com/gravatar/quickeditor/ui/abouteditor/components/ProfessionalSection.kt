package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.abouteditor.AboutInputField
import com.gravatar.quickeditor.ui.abouteditor.ProfessionalFields

@Composable
internal fun ProfessionalSection(
    professionalFields: ProfessionalFields,
    formEnabled: Boolean,
    onValueChange: (AboutInputField) -> Unit,
) {
    Column {
        AboutEditSectionLabel(
            title = stringResource(R.string.gravatar_qe_about_field_section_label_professional),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        AboutEditField(
            label = stringResource(professionalFields.jobTitle.labelRes),
            value = professionalFields.jobTitle.value,
            enabled = formEnabled,
            maxLines = professionalFields.jobTitle.maxLines,
            description = professionalFields.jobTitle.descriptionRes?.let { stringResource(it) },
            onValueChange = {
                onValueChange(professionalFields.jobTitle.copy(value = it))
            },
        )
        AboutEditField(
            label = stringResource(professionalFields.company.labelRes),
            value = professionalFields.company.value,
            enabled = formEnabled,
            maxLines = professionalFields.company.maxLines,
            description = professionalFields.company.descriptionRes?.let { stringResource(it) },
            onValueChange = {
                onValueChange(professionalFields.company.copy(value = it))
            },
        )
    }
}
