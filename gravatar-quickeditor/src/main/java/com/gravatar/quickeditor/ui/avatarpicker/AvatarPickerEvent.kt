package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import com.gravatar.restapi.models.Avatar

internal sealed class AvatarPickerEvent {
    data object Refresh : AvatarPickerEvent()

    data class AvatarSelected(val avatar: Avatar) : AvatarPickerEvent()

    data class LocalImageSelected(val uri: Uri) : AvatarPickerEvent()

    data class ImageCropped(val uri: Uri) : AvatarPickerEvent()

    data class FailedAvatarTapped(val uri: Uri) : AvatarPickerEvent()

    data class FailedAvatarDismissed(val uri: Uri) : AvatarPickerEvent()

    data object FailedAvatarDialogDismissed : AvatarPickerEvent()

    data object HandleAuthFailureTapped : AvatarPickerEvent()

    data class DownloadAvatarTapped(val avatar: Avatar) : AvatarPickerEvent()

    data object DownloadManagerDisabledDialogDismissed : AvatarPickerEvent()

    data class AvatarDeleteSelected(val avatarId: String) : AvatarPickerEvent()

    data object AvatarDeleteAlertDismissed : AvatarPickerEvent()

    data class AvatarRatingSelected(val avatarId: String, val rating: Avatar.Rating) : AvatarPickerEvent()
}
