package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
private fun SelectableAvatar(
    imageUrl: String,
    isSelected: Boolean,
    onAvatarClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 7.dp
    GravatarTheme {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = R.string.selectable_avatar_content_description),
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .then(
                    if (isSelected) {
                        Modifier.border(7.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(cornerRadius))
                    } else {
                        Modifier.border(2.dp, MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(cornerRadius))
                    },
                )
                .clickable {
                    onAvatarClicked()
                },
        )
    }
}

@Preview
@Composable
private fun SelectableAvatarNotSelectedPreview() {
    SelectableAvatar("https://gravatar.com/avatar/25e98fbcc79879af856ad1cd6881dcdd", false, {}, Modifier.size(150.dp))
}

@Preview
@Composable
private fun SelectableAvatarSelectedPreview() {
    SelectableAvatar("https://gravatar.com/avatar/25e98fbcc79879af856ad1cd6881dcdd", true, {}, Modifier.size(150.dp))
}
