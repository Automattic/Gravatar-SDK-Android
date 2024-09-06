package com.gravatar.quickeditor.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.QuickEditorFileProvider
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (Avatar) -> Unit,
    onLocalImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var photoImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onLocalImageSelected(it) }
    }
    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val takenPictureUri = photoImageUri
        if (success && takenPictureUri != null) onLocalImageSelected(takenPictureUri)
    }

    when (state.contentLayout) {
        ContentLayout.Vertical -> {
            VerticalAvatarsSection(
                state = state,
                modifier = modifier,
                onAvatarSelected = onAvatarSelected,
                onChoosePhotoClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onTakePhotoClick = {
                    val imageUri = QuickEditorFileProvider.getTempCameraImageUri(context)
                    photoImageUri = imageUri
                    takePhoto.launch(imageUri)
                },
            )
        }

        ContentLayout.Horizontal -> {
            HorizontalAvatarsSection(
                state = state,
                modifier = modifier,
                onAvatarSelected = onAvatarSelected,
                onTakePhotoClick = {
                    val imageUri = QuickEditorFileProvider.getTempCameraImageUri(context)
                    photoImageUri = imageUri
                    takePhoto.launch(imageUri)
                },
                onChoosePhotoClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            )
        }
    }
}

internal val AvatarsSectionUiState.titleRes: Int
    @StringRes get() = if (avatars.isNotEmpty()) {
        R.string.avatar_picker_title
    } else {
        R.string.avatar_picker_title_empty_state
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
                            updatedDate = null
                        },
                        isSelected = true,
                        isLoading = false,
                    ),
                ),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Horizontal,
            ),
            onAvatarSelected = { },
            onLocalImageSelected = { },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarSectionGridPreview() {
    GravatarTheme {
        AvatarsSection(
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
            onAvatarSelected = { },
            onLocalImageSelected = { },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarSectionEmptyPreview() {
    GravatarTheme {
        AvatarsSection(
            state = AvatarsSectionUiState(
                avatars = emptyList(),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                contentLayout = ContentLayout.Horizontal,
            ),
            onAvatarSelected = { },
            onLocalImageSelected = { },
        )
    }
}
