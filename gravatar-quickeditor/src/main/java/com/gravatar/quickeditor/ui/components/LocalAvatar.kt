package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun LocalAvatar(imageUri: String, isLoading: Boolean, modifier: Modifier = Modifier) {
    SelectableAvatar(
        imageUrl = imageUri,
        isSelected = false,
        isLoading = isLoading,
        onAvatarClicked = { },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun LocalAvatarPreview() {
    LocalAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isLoading = false,
        Modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun LocalAvatarLoadingPreview() {
    LocalAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isLoading = true,
        Modifier.size(150.dp),
    )
}
