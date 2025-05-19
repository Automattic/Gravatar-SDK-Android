package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AboutEditField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    description: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = maxLines,
            singleLine = maxLines == 1,
            enabled = enabled,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(2.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    innerTextField()
                }
            },
        )
        if (!description.isNullOrEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditFieldNoDescriptionPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            AboutEditField(
                value = "John Doe",
                description = null,
                enabled = true,
                label = "Display name",
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditFieldDescriptionPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            AboutEditField(
                value = """
                    Oceanographer, Filmmaker, and Connoisseur of Red Beanies. 
                    Diving deep in pursuit of underwater wonders. 
                    While oceanographer Steve Zissou is working on his latest,
                """.trimIndent(),
                description = "Brief description for your profile.",
                enabled = true,
                label = "About me",
                maxLines = 4,
                onValueChange = {},
            )
        }
    }
}
