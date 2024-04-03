package com.gravatar.demoapp.ui.model

import com.gravatar.DefaultAvatarOption
import com.gravatar.ImageRating

data class SettingsState(
    val email: String,
    val size: Int?,
    val defaultAvatarImageEnabled: Boolean,
    val selectedDefaultAvatar: DefaultAvatarOption,
    val defaultAvatarOptions: List<DefaultAvatarOption>,
    val forceDefaultAvatar: Boolean,
    val imageRatingEnabled: Boolean,
    val imageRating: ImageRating,
)
