package com.gravatar

/**
 * Data class that represents the query options for an avatar.
 *
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarOption Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 */
public data class AvatarQueryOptions(
    public val preferredSize: Int? = null,
    public val defaultAvatarOption: DefaultAvatarOption? = null,
    public val rating: ImageRating? = null,
    public val forceDefaultAvatar: Boolean? = null,
)
