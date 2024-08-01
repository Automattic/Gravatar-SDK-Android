package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.restapi.models.Avatar

internal data class AvatarPickerUiState(
    val avatars: List<Avatar> = emptyList(),
    val error: Boolean = false,
)
