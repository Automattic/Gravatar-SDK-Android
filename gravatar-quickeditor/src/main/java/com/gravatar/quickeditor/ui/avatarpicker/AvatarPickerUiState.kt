package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState

internal data class AvatarPickerUiState(
    val email: Email,
    val avatars: List<Avatar>? = null,
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val profile: ComponentState<Profile>? = null,
)
