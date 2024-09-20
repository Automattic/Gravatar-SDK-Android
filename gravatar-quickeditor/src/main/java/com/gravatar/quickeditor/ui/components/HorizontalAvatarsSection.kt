package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme

@Composable
internal fun HorizontalAvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (Avatar) -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var popupVisible by remember { mutableStateOf(false) }
    var popupAnchorPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    var popupAnchorHeight by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    LaunchedEffect(state.scrollToIndex) {
        state.scrollToIndex?.let { listState.scrollToItem(it) }
    }

    val sectionPadding = 16.dp
    Surface(
        modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(8.dp),
        ),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(vertical = sectionPadding),
            ) {
                QESectionTitle(
                    title = stringResource(id = state.titleRes),
                    modifier = Modifier
                        .padding(horizontal = sectionPadding),
                )
                QESectionMessage(
                    message = stringResource(R.string.avatar_picker_description),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = sectionPadding),
                )
                if (state.avatars.isEmpty()) {
                    ListEmptyStateBox()
                } else {
                    LazyAvatarRow(
                        avatars = state.avatars,
                        onAvatarSelected = onAvatarSelected,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 24.dp),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = sectionPadding),
                    )
                }
                QEButton(
                    buttonText = stringResource(id = R.string.avatar_picker_upload_image),
                    onClick = { popupVisible = true },
                    enabled = state.uploadButtonEnabled,
                    modifier = Modifier
                        .padding(horizontal = sectionPadding)
                        .onSizeChanged { intSize: IntSize -> popupAnchorHeight = intSize.height }
                        .onGloballyPositioned { layoutCoordinates ->
                            popupAnchorPosition = layoutCoordinates
                                .positionInRoot()
                                .round()
                        },
                )
            }
            if (popupVisible) {
                MediaPickerPopup(
                    alignment = Alignment.BottomCenter,
                    onDismissRequest = { popupVisible = false },
                    offset = calculatePopupOffsetForFullWidthButton(popupAnchorPosition, popupAnchorHeight),
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
internal fun calculatePopupOffsetForFullWidthButton(popupAnchorPosition: IntOffset, popupAnchorHeight: Int): IntOffset {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    return IntOffset(0, -(screenHeight.dpToPx().toInt() - popupAnchorPosition.y + popupAnchorHeight / 1.5).toInt())
}

@Composable
internal fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@Preview(showBackground = true)
private fun HorizontalAvatarSectionPreview() {
    GravatarTheme {
        HorizontalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = List(6) {
                    AvatarUi.Uploaded(
                        avatar = Avatar {
                            imageUrl = "/image/url"
                            imageId = it.toString()
                            rating = Avatar.Rating.G
                            altText = "alt"
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
private fun HorizontalAvatarSectionEmptyPreview() {
    GravatarTheme {
        HorizontalAvatarsSection(
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
