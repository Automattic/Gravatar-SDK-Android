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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme
import java.net.URI

@Composable
internal fun HorizontalAvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (AvatarUi) -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.scrollToIndex) {
        state.scrollToIndex?.let {
            listState.scrollToItem(it)
        }
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
                    message = stringResource(R.string.gravatar_qe_avatar_picker_description),
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
                        onAvatarOptionClicked = onAvatarOptionClicked,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 24.dp),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = sectionPadding),
                    )
                }
                UploadImageButton(
                    onTakePhotoClick = onTakePhotoClick,
                    onChoosePhotoClick = onChoosePhotoClick,
                    enabled = state.uploadButtonEnabled,
                    modifier = Modifier
                        .padding(horizontal = sectionPadding),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HorizontalAvatarSectionPreview() {
    GravatarTheme {
        HorizontalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = List(6) {
                    AvatarUi.Uploaded(
                        avatar = Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
        )
    }
}
