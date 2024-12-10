package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.restapi.models.Avatar
import java.net.URL

@Composable
internal fun LazyAvatarRow(
    avatars: List<AvatarUi>,
    onAvatarSelected: (AvatarUi) -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
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
            Avatar(
                avatar = avatarModel,
                onAvatarSelected = { onAvatarSelected(avatarModel) },
                onAvatarOptionClicked = { avatar, option -> onAvatarOptionClicked(avatar, option) },
                size = avatarSize,
                modifier = Modifier
                    .animateItem()
                    .size(avatarSize),
            )
        }
    }
}

internal val avatarSize = 96.dp

@Composable
internal fun Avatar(
    avatar: AvatarUi,
    size: Dp,
    onAvatarSelected: () -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    modifier: Modifier,
) {
    when (avatar) {
        is AvatarUi.Uploaded -> {
            val sizePx = with(LocalDensity.current) { size.roundToPx() }
            SelectableAvatar(
                imageUrl = avatar.imageUrlWithSize(sizePx),
                isSelected = avatar.isSelected,
                loadingState = avatar.loadingState,
                onAvatarClicked = { onAvatarSelected() },
                onAvatarOptionClicked = { onAvatarOptionClicked(avatar.avatar, it) },
                modifier = modifier,
            )
        }

        is AvatarUi.Local -> SelectableAvatar(
            imageUrl = avatar.uri.toString(),
            isSelected = false,
            loadingState = avatar.loadingState,
            onAvatarClicked = { onAvatarSelected() },
            modifier = modifier,
        )
    }
}

private fun AvatarUi.Uploaded.imageUrlWithSize(sizePx: Int) = avatar.imageUrl.toURL()?.let { url ->
    URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
}.toString()

private val AvatarUi.Uploaded.loadingState: AvatarLoadingState
    get() = if (isLoading) AvatarLoadingState.Loading else AvatarLoadingState.None

private val AvatarUi.Local.loadingState: AvatarLoadingState
    get() = when {
        isLoading -> AvatarLoadingState.Loading
        else -> AvatarLoadingState.Failure
    }
