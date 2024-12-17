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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.gravatar.ui.GravatarTheme

@Composable
internal fun CtaSection(
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            title?.let { QESectionTitle(title = it) }
            QESectionMessage(message = message, modifier = Modifier.padding(top = 4.dp))
            QEButton(
                buttonText = buttonText,
                onClick = onButtonClick,
                modifier = Modifier.padding(top = if (title == null) 16.dp else 24.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CtaSectionPreview() {
    GravatarTheme {
        Surface {
            CtaSection(
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

@PreviewLightDark
@Composable
private fun CtaSectionNoTitlePreview() {
    GravatarTheme {
        Surface {
            CtaSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                message = "Manage your profile for the web in one place.",
                buttonText = "Continue",
                onButtonClick = {},
            )
        }
    }
}
