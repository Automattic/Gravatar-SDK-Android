package com.gravatar.utils

import androidx.compose.ui.util.fastFirstOrNull
import com.gravatar.models.UserProfile

public fun UserProfile.getPrimaryEmail(): String? {
    return this.emails.fastFirstOrNull { it.primary ?: false }?.value
}

public fun UserProfile.getDisplayName(): String? {
    return this.displayName ?: this.name?.formatted ?: this.preferredUsername
}
