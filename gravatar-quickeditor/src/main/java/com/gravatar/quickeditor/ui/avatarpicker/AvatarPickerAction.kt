package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.restapi.models.Avatar

internal sealed class AvatarPickerAction {
    data class AvatarSelected(val avatar: Avatar) : AvatarPickerAction()
}
