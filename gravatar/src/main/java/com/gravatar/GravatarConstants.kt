package com.gravatar

object GravatarConstants {
    // Gravatar image / avatar
    const val GRAVATAR_IMAGE_BASE_URL = "https://www.gravatar.com/"
    const val GRAVATAR_IMAGE_HOST = "www.gravatar.com"
    const val GRAVATAR_IMAGE_PATH = "avatar"
    const val GRAVATAR_IMAGE_RAW_HOST = "gravatar.com"

    // Gravatar API
    const val GRAVATAR_API_BASE_URL = "https://api.gravatar.com/v1/"

    // Minimum and maximum size of the avatar
    const val MINIMUM_AVATAR_SIZE = 1
    const val MAXIMUM_AVATAR_SIZE = 2048
    val AVATAR_SIZE_RANGE = MINIMUM_AVATAR_SIZE..MAXIMUM_AVATAR_SIZE
}
