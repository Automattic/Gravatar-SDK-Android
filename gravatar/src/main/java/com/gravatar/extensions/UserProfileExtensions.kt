package com.gravatar.extensions

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
public fun UserProfile.avatarUrl(): AvatarUrl {
    return AvatarUrl(this.hash())
}

/**
 * Get the profile URL for a user profile.
 */
public fun UserProfile.profileUrl(): ProfileUrl {
    return ProfileUrl(this.hash())
}
