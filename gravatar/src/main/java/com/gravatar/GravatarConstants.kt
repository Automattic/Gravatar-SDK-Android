package com.gravatar

object GravatarConstants {
    /** Gravatar image base URL */
    const val GRAVATAR_IMAGE_BASE_URL = "https://www.gravatar.com/"

    /** Gravatar image host */
    const val GRAVATAR_IMAGE_HOST = "www.gravatar.com"

    /** Gravatar image path */
    const val GRAVATAR_IMAGE_PATH = "avatar"

    /** Gravatar image base host */
    const val GRAVATAR_IMAGE_BASE_HOST = "gravatar.com"

    /** Gravatar API base URL */
    const val GRAVATAR_API_BASE_URL = "https://api.gravatar.com/v1/"

    /** Minimum size of the avatar */
    const val MINIMUM_AVATAR_SIZE = 1

    /** Maximum size of the avatar */
    const val MAXIMUM_AVATAR_SIZE = 2048

    /** Range of the avatar size */
    val AVATAR_SIZE_RANGE = MINIMUM_AVATAR_SIZE..MAXIMUM_AVATAR_SIZE
}
