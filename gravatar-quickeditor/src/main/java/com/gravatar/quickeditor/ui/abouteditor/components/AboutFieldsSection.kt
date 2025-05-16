package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorField

@Composable
internal fun AboutFieldsSection(
    label: String,
    fields: Set<AboutEditorField>,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        AboutEditSectionLabel(
            title = label,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        fields.forEach { editorField ->
            AboutEditField(
                label = stringResource(editorField.labelRes),
                value = editorField.value,
                enabled = formEnabled,
                maxLines = editorField.maxLines,
                description = editorField.descriptionRes?.let { stringResource(it) },
                onValueChange = {
                    onValueChange(editorField.copy(value = it))
                },
            )
        }
    }
}
