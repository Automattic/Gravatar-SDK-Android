package com.gravatar.quickeditor.ui.components

import android.Manifest
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.QuickEditorFileProvider
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.hasCameraPermissionInManifest
import com.gravatar.quickeditor.ui.openAppPermissionSettings
import com.gravatar.quickeditor.ui.withPermission
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme
import java.net.URI

@Composable
internal fun AvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (AvatarUi) -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    onLocalImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var cameraPermissionDialogRationaleVisible by rememberSaveable { mutableStateOf(false) }
    var photoImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onLocalImageSelected(it) }
    }
    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val takenPictureUri = photoImageUri
        if (success && takenPictureUri != null) onLocalImageSelected(takenPictureUri)
    }

    val takePhotoCallback = {
        val imageUri = QuickEditorFileProvider.getTempCameraImageUri(context)
        photoImageUri = imageUri
        takePhoto.launch(imageUri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            takePhotoCallback()
        } else {
            cameraPermissionDialogRationaleVisible = true
        }
    }

    val permissionAwareTakePhotoCallback = {
        if (context.hasCameraPermissionInManifest()) {
            context.withPermission(
                permission = Manifest.permission.CAMERA,
                onRequestPermission = {
                    cameraPermissionLauncher.launch(it)
                },
                onShowRationale = { cameraPermissionDialogRationaleVisible = true },
                grantedCallback = {
                    takePhotoCallback()
                },
            )
        } else {
            takePhotoCallback()
        }
    }

    when (state.avatarPickerContentLayout) {
        AvatarPickerContentLayout.Vertical -> {
            VerticalAvatarsSection(
                state = state,
                modifier = modifier,
                onAvatarSelected = onAvatarSelected,
                onAvatarOptionClicked = onAvatarOptionClicked,
                onChoosePhotoClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onTakePhotoClick = permissionAwareTakePhotoCallback,
            )
        }

        AvatarPickerContentLayout.Horizontal -> {
            HorizontalAvatarsSection(
                state = state,
                modifier = modifier,
                onAvatarSelected = onAvatarSelected,
                onAvatarOptionClicked = onAvatarOptionClicked,
                onTakePhotoClick = permissionAwareTakePhotoCallback,
                onChoosePhotoClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            )
        }
    }

    PermissionRationaleDialog(
        isVisible = cameraPermissionDialogRationaleVisible,
        message = stringResource(R.string.gravatar_qe_camera_permission_rationale_message),
        onDismiss = { cameraPermissionDialogRationaleVisible = false },
        onConfirmation = {
            cameraPermissionDialogRationaleVisible = false
            context.openAppPermissionSettings()
        },
    )
}

internal val AvatarsSectionUiState.titleRes: Int
    @StringRes get() = if (avatars.isNotEmpty()) {
        R.string.gravatar_qe_avatar_picker_title
    } else {
        R.string.gravatar_qe_avatar_picker_title_empty_state
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
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
                            imageId = "1"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                        isSelected = true,
                        isLoading = false,
                    ),
                ),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
            ),
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
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
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
            ),
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
            onLocalImageSelected = { },
        )
    }
}
