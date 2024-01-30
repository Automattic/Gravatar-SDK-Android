package com.gravatar.demoapp.ui.model

import com.gravatar.DefaultAvatarImage
import com.gravatar.ImageRating

data class SettingsState(
    val email: String,
    val size: Int?,
    val defaultAvatarImageEnabled: Boolean,
    val selectedDefaultAvatar: DefaultAvatarImage,
    val defaultAvatarOptions: List<DefaultAvatarImage>,
    val forceDefaultAvatar: Boolean,
    val imageRatingEnabled: Boolean,
    val imageRating: ImageRating,
)
