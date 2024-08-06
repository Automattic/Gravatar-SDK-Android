package com.gravatar.quickeditor.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme

@Composable
internal fun EmailLabel(email: Email, modifier: Modifier = Modifier) {
    Text(
        text = email.toString(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        fontSize = 13.sp,
        maxLines = 1,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun EmailLabelPreview() {
    GravatarTheme {
        EmailLabel(email = Email("william.henry.harrison@example.com"))
    }
}
