package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import com.gravatar.restapi.models.Avatar
import java.io.File

internal sealed class AvatarPickerAction {
    data object AvatarSelected : AvatarPickerAction()

    data class LaunchImageCropper(val imageUri: Uri, val tempFile: File) : AvatarPickerAction()

    data object AvatarSelectionFailed : AvatarPickerAction()

    data object InvokeAuthFailed : AvatarPickerAction()

    data class AvatarDeletionFailed(val avatar: Avatar) : AvatarPickerAction()

    data object AvatarDownloadStarted : AvatarPickerAction()

    data object DownloadManagerNotAvailable : AvatarPickerAction()
}
