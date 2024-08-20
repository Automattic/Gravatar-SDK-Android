package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QEButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(14.dp),
        enabled = enabled,
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview
@Composable
private fun QEButtonPreview() {
    GravatarTheme {
        Surface {
            QEButton(
                modifier = Modifier.padding(20.dp),
                buttonText = stringResource(id = R.string.avatar_picker_upload_image),
                onClick = { },
                enabled = true,
            )
        }
    }
}
