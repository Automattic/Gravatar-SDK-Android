package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.ui.GravatarTheme

@Composable
internal fun ErrorSection(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(16.dp),
    ) {
        Column {
            QESectionTitle(title = title)
            QESectionMessage(message = message, modifier = Modifier.padding(top = 4.dp))
            QEButton(buttonText = buttonText, onClick = onButtonClick, modifier = Modifier.padding(top = 24.dp))
        }
    }
}

@Preview
@Composable
private fun ErrorSectionPreview() {
    GravatarTheme {
        Surface {
            ErrorSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                title = "Oooops",
                message = "Something went wrong and we couldn't connect to Gravatar servers.",
                buttonText = "Retry",
                onButtonClick = {},
            )
        }
    }
}
