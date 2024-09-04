package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import com.gravatar.restapi.models.Avatar
import java.io.File

internal sealed class AvatarPickerAction {
    data class AvatarSelected(val avatar: Avatar) : AvatarPickerAction()

    data class AvatarUploadFailed(val imageUri: Uri) : AvatarPickerAction()

    data class LaunchImageCropper(val imageUri: Uri, val tempFile: File) : AvatarPickerAction()

    data object AvatarSelectionFailed : AvatarPickerAction()

    data object InvokeAuthFailed : AvatarPickerAction()
}
