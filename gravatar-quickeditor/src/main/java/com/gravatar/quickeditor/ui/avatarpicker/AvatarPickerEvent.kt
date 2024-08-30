package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import com.gravatar.restapi.models.Avatar

internal sealed class AvatarPickerEvent {
    data object Refresh : AvatarPickerEvent()

    data class AvatarSelected(val avatar: Avatar) : AvatarPickerEvent()

    data class LocalImageSelected(val uri: Uri) : AvatarPickerEvent()

    data class ImageCropped(val uri: Uri) : AvatarPickerEvent()

    data object LoginUserTapped : AvatarPickerEvent()
}
