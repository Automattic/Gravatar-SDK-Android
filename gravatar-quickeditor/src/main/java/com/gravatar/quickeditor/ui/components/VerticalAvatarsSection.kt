package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme

@Composable
internal fun VerticalAvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (Avatar) -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var popupVisible by rememberSaveable { mutableStateOf(false) }
    var popupAnchorY by rememberSaveable { mutableIntStateOf(0) }
    var popupAnchorHeight by rememberSaveable { mutableIntStateOf(0) }
    val gridState = rememberLazyGridState()

    val sectionPadding = 16.dp
    val itemSpacing = 8.dp
    Surface(modifier = modifier) {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(8.dp),
                ),
                state = gridState,
                contentPadding = PaddingValues(sectionPadding),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                item(
                    span = { GridItemSpan((maxLineSpan)) },
                ) {
                    QESectionTitle(
                        title = stringResource(id = state.titleRes),
                        modifier = Modifier,
                    )
                }
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    QESectionMessage(
                        message = stringResource(R.string.avatar_picker_description),
                        modifier = Modifier
                            .padding(top = 4.dp),
                    )
                }
                items(items = state.avatars, key = { it.avatarId }) { avatarModel ->
                    avatarModel.avatar(
                        onAvatarSelected = onAvatarSelected,
                        modifier = Modifier,
                    )
                }
                if (state.avatars.isEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        ListEmptyStateBox()
                    }
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        QEButton(
                            buttonText = stringResource(id = R.string.avatar_picker_upload_image),
                            onClick = { popupVisible = true },
                            enabled = state.uploadButtonEnabled,
                            modifier = Modifier
                                .onSizeChanged { intSize: IntSize -> popupAnchorHeight = intSize.height }
                                .onPlaced { layoutCoordinates -> popupAnchorY = layoutCoordinates.size.height },
                        )
                    }
                }
            }
            if (popupVisible) {
                MediaPickerPopup(
                    alignment = Alignment.BottomCenter,
                    onDismissRequest = { popupVisible = false },
                    offset = IntOffset(0, -popupAnchorY - popupAnchorHeight / 2),
                    onChoosePhotoClick = {
                        popupVisible = false
                        onChoosePhotoClick()
                    },
                    onTakePhotoClick = {
                        popupVisible = false
                        onTakePhotoClick()
                    },
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun VerticalAvatarSectionPreview() {
    GravatarTheme {
        VerticalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = List(6) {
                    AvatarUi.Uploaded(
                        avatar = Avatar {
                            imageUrl = "/image/url"
                            format = 0
                            imageId = it.toString()
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = ""
                        },
                        isSelected = it == 0,
                        isLoading = false,
                    )
                },
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun VerticalAvatarSectionEmptyPreview() {
    GravatarTheme {
        VerticalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = emptyList(),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
        )
    }
}
