package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.quickeditor.R

private val cornerRadius = 8.dp

@Composable
internal fun SelectableAvatar(
    imageUrl: String,
    isSelected: Boolean,
    loadingState: AvatarLoadingState,
    onAvatarClicked: () -> Unit,
    onAltTextClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 8.dp
    var moreOptionsPopupVisible by remember { mutableStateOf(false) }
    var popupAnchorBounds: Rect by remember { mutableStateOf(Rect(Offset.Zero, Size.Zero)) }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isSelected) {
                    Modifier.border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(cornerRadius))
                } else {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceDim,
                        RoundedCornerShape(cornerRadius),
                    )
                },
            )
            .clickable {
                onAvatarClicked()
            }
            .onGloballyPositioned { layoutCoordinates ->
                popupAnchorBounds = layoutCoordinates
                    .boundsInRoot()
            },
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = R.string.gravatar_qe_selectable_avatar_content_description),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
        )
        when (loadingState) {
            AvatarLoadingState.None -> LoadedOverlay({
                moreOptionsPopupVisible = true
            }, Modifier.align(Alignment.BottomEnd))

            AvatarLoadingState.Loading -> LoadingOverlay()
            is AvatarLoadingState.Failure -> FailureOverlay()
        }

        if (moreOptionsPopupVisible) {
            AvatarMoreOptionsPickerPopup(
                anchorAlignment = Alignment.CenterHorizontally,
                anchorBounds = popupAnchorBounds,
                onDismissRequest = { moreOptionsPopupVisible = false },
                onAltTextClick = {
                    moreOptionsPopupVisible = false
                    onAltTextClicked()
                },
            )
        }
    }
}

@Composable
private fun LoadedOverlay(onMoreOptionsClicked: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable(
                onClick = onMoreOptionsClicked,
            )
            .size(24.dp)
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(2.dp),
            )
            .padding(5.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.gravatar_avatar_more_options_dots),
            contentDescription =
                stringResource(id = R.string.gravatar_qe_selectable_avatar_more_options_content_description),
            tint = Color.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun LoadingOverlay(modifier: Modifier = Modifier) {
    Overlay(modifier) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun FailureOverlay(modifier: Modifier = Modifier) {
    Overlay(modifier) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = stringResource(R.string.gravatar_qe_failed_to_load_avatar_content_description),
            tint = Color.White,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun Overlay(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cornerRadius),
            ),
    ) {
        content()
    }
}

internal sealed class AvatarLoadingState {
    data object None : AvatarLoadingState()

    data object Loading : AvatarLoadingState()

    data object Failure : AvatarLoadingState()
}

@Preview
@Composable
private fun SelectableAvatarNotSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        loadingState = AvatarLoadingState.None,
        onAvatarClicked = { },
        onAltTextClicked = { },
        modifier = Modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun SelectableAvatarSelectedPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = true,
        loadingState = AvatarLoadingState.None,
        onAvatarClicked = { },
        onAltTextClicked = { },
        modifier = Modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun SelectableAvatarLoadingPreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        loadingState = AvatarLoadingState.Loading,
        onAvatarClicked = { },
        onAltTextClicked = { },
        modifier = Modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun SelectableAvatarFailurePreview() {
    SelectableAvatar(
        "https://gravatar.com/avatar/fd2188b818f15e629f7b62896b5c6075?s=250",
        isSelected = false,
        loadingState = AvatarLoadingState.Failure,
        onAvatarClicked = { },
        onAltTextClicked = { },
        modifier = Modifier.size(150.dp),
    )
}
