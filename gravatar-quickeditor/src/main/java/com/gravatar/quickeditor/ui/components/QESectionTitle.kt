package com.gravatar.quickeditor.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QESectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun QESectionTitlePreview() {
    GravatarTheme {
        QESectionTitle(title = stringResource(id = R.string.gravatar_qe_avatar_picker_title))
    }
}
