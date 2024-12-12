package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import java.io.File

internal sealed class AvatarPickerAction {
    data object AvatarSelected : AvatarPickerAction()

    data class LaunchImageCropper(val imageUri: Uri, val tempFile: File) : AvatarPickerAction()

    data object AvatarSelectionFailed : AvatarPickerAction()

    data object InvokeAuthFailed : AvatarPickerAction()

    data object AvatarDownloadStarted : AvatarPickerAction()

    data object DownloadManagerNotAvailable : AvatarPickerAction()

    data class AvatarDeletionFailed(val avatarId: String) : AvatarPickerAction()

    data class AvatarUpdated(val type: AvatarUpdateType) : AvatarPickerAction()

    data class AvatarUpdateFailed(val type: AvatarUpdateType) : AvatarPickerAction()
}

internal enum class AvatarUpdateType {
    RATING,
}
