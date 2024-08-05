package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email

internal data class AvatarPickerUiState(
    val email: Email,
    val avatars: List<Avatar> = emptyList(),
    val error: Boolean = false,
)
