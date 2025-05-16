package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorField
import com.gravatar.quickeditor.ui.abouteditor.PersonalFields

@Composable
internal fun PersonalSection(
    personalFields: PersonalFields,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
) {
    Column {
        AboutEditSectionLabel(
            title = stringResource(R.string.gravatar_qe_about_field_section_label_personal),
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp),
        )
        if (personalFields.displayName.visible) {
            AboutEditField(
                label = stringResource(personalFields.displayName.labelRes),
                value = personalFields.displayName.value,
                enabled = formEnabled,
                maxLines = personalFields.displayName.maxLines,
                description = personalFields.displayName.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(personalFields.displayName.copy(value = it))
                },
            )
        }
        if (personalFields.aboutMe.visible) {
            AboutEditField(
                label = stringResource(personalFields.aboutMe.labelRes),
                value = personalFields.aboutMe.value,
                enabled = formEnabled,
                maxLines = personalFields.aboutMe.maxLines,
                description = personalFields.aboutMe.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(personalFields.aboutMe.copy(value = it))
                },
            )
        }
        if (personalFields.pronunciation.visible) {
            AboutEditField(
                label = stringResource(personalFields.pronunciation.labelRes),
                value = personalFields.pronunciation.value,
                enabled = formEnabled,
                maxLines = personalFields.pronunciation.maxLines,
                description = personalFields.pronunciation.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(personalFields.pronunciation.copy(value = it))
                },
            )
        }
        if (personalFields.pronouns.visible) {
            AboutEditField(
                label = stringResource(personalFields.pronouns.labelRes),
                value = personalFields.pronouns.value,
                enabled = formEnabled,
                maxLines = personalFields.pronouns.maxLines,
                description = personalFields.pronouns.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(personalFields.pronouns.copy(value = it))
                },
            )
        }
        if (personalFields.location.visible) {
            AboutEditField(
                label = stringResource(personalFields.location.labelRes),
                value = personalFields.location.value,
                enabled = formEnabled,
                maxLines = personalFields.location.maxLines,
                description = personalFields.location.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(personalFields.location.copy(value = it))
                },
            )
        }
    }
}
