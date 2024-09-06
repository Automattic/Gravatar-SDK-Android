package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.restapi.models.Avatar

@Composable
internal fun LazyAvatarRow(
    avatars: List<AvatarUi>,
    onAvatarSelected: (Avatar) -> Unit,
    horizontalArrangement: Arrangement.Horizontal,
    state: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        items(items = avatars, key = { it.avatarId }) { avatarModel ->
            avatarModel.avatar(
                onAvatarSelected = onAvatarSelected,
                modifier = Modifier.size(avatarSize),
            )
        }
    }
}

internal val avatarSize = 96.dp

@Composable
internal fun AvatarUi.avatar(onAvatarSelected: (Avatar) -> Unit, modifier: Modifier) {
    when (this) {
        is AvatarUi.Uploaded -> SelectableAvatar(
            imageUrl = avatar.imageUrl,
            isSelected = isSelected,
            isLoading = isLoading,
            onAvatarClicked = {
                onAvatarSelected(avatar)
            },
            modifier = modifier,
        )

        is AvatarUi.Local -> LocalAvatar(
            imageUri = uri.toString(),
            isLoading = true,
            modifier = modifier,
        )
    }
}
