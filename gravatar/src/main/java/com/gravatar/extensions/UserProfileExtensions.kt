package com.gravatar.extensions

import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.ProfileUrl
import com.gravatar.api.models.UserProfile
import com.gravatar.types.Hash

/**
 * Get the hash for a user profile.
 */
public fun UserProfile.hash(): Hash {
    return Hash(this.hash)
}

/**
 * Get the avatar URL for a user profile.
 */
public fun UserProfile.avatarUrl(avatarQueryOptions: AvatarQueryOptions? = null): AvatarUrl {
    return AvatarUrl(this.hash(), avatarQueryOptions)
}

/**
 * Get the profile URL for a user profile.
 */
public fun UserProfile.profileUrl(): ProfileUrl {
    return ProfileUrl(this.hash())
}

/**
 * Get formatted user info for a user profile.
 */
public fun UserProfile.formattedUserInfo(): String {
    val line1 = arrayOf(this.jobTitle, this.company).filter { !it.isNullOrBlank() }.joinToString(", ")
    val line2 = arrayOf(this.pronunciation, this.pronouns, this.currentLocation).filter {
        !it.isNullOrBlank()
    }.joinToString(" · ")
    return arrayOf(line1, line2).filter { it.isNotBlank() }.joinToString("\n")
}
