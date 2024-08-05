package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email

internal data class AvatarPickerUiState(
    val email: Email,
    val avatars: List<Avatar>? = null,
    val isLoading: Boolean = false,
    val error: Boolean = false,
)
