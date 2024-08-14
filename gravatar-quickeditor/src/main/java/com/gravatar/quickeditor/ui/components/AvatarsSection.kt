package com.gravatar.quickeditor.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.quickeditor.QuickEditorFileProvider
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.avatarpicker.fullUrl
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme
import java.time.Instant

@Composable
internal fun AvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (Avatar) -> Unit,
    onLocalImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var popupVisible by rememberSaveable { mutableStateOf(false) }
    var popupYOffset by rememberSaveable { mutableIntStateOf(0) }
    var photoImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onLocalImageSelected(it) }
    }

    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val takenPictureUri = photoImageUri
        if (success && takenPictureUri != null) onLocalImageSelected(takenPictureUri)
    }

    LaunchedEffect(state.scrollToIndex) {
        state.scrollToIndex?.let { listState.scrollToItem(it) }
    }

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.avatar_picker_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.avatar_picker_description),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp),
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 24.dp),
                state = listState,
            ) {
                items(items = state.avatars, key = { it.avatarId }) { avatarModel ->
                    when (avatarModel) {
                        is AvatarUi.Uploaded -> SelectableAvatar(
                            imageUrl = avatarModel.avatar.fullUrl,
                            isSelected = avatarModel.isSelected,
                            isLoading = avatarModel.isLoading,
                            onAvatarClicked = {
                                onAvatarSelected(avatarModel.avatar)
                            },
                            modifier = Modifier.size(96.dp),
                        )

                        is AvatarUi.Local -> LocalAvatar(
                            imageUri = avatarModel.uri.toString(),
                            isLoading = true,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .onPlaced { layoutCoordinates -> popupYOffset = layoutCoordinates.size.height },
                onClick = { popupVisible = true },
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(14.dp),
                enabled = state.uploadButtonEnabled,
            ) {
                Text(
                    text = stringResource(R.string.avatar_picker_upload_image),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        if (popupVisible) {
            MediaPickerPopup(
                alignment = Alignment.BottomCenter,
                onDismissRequest = { popupVisible = false },
                offset = IntOffset(0, -popupYOffset - 30),
                onChoosePhotoClick = {
                    popupVisible = false
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onTakePhotoClick = {
                    popupVisible = false
                    val imageUri = QuickEditorFileProvider.getTempCameraImageUri(context)
                    photoImageUri = imageUri
                    takePhoto.launch(imageUri)
                },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarSectionPreview() {
    GravatarTheme {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = listOf(
                    AvatarUi.Uploaded(
                        avatar = Avatar {
                            imageUrl = "/image/url"
                            format = 0
                            imageId = "1"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = Instant.now()
                        },
                        isSelected = true,
                        isLoading = false,
                    ),
                ),
                scrollToIndex = null,
                uploadButtonEnabled = true,
            ),
            onAvatarSelected = { },
            onLocalImageSelected = { },
        )
    }
}
