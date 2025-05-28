package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorField
import com.gravatar.quickeditor.ui.editor.AboutInputField

@Composable
internal fun AboutFieldsSection(
    label: String?,
    fields: Set<AboutEditorField>,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
    modifier: Modifier = Modifier,
    sectionDescription: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        label?.let {
            Column {
                AboutEditSectionLabel(
                    title = it,
                )
                sectionDescription?.let {
                    Text(
                        text = sectionDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
private fun AboutFieldsSectionPreview() {
    AboutFieldsSection(
        label = "Extras",
        sectionDescription = "This information will not appear on your Gravatar " +
            "Web Profile, but other apps and services can use it.",
        fields = setOf(
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
        onValueChange = {},
    )
}
