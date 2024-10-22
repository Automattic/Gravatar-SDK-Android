package com.gravatar.quickeditor.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QESectionMessage(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun QESectionMessagePreview() {
    GravatarTheme {
        QESectionMessage(message = stringResource(id = R.string.gravatar_qe_avatar_picker_description))
    }
}
