package com.gravatar

sealed class DefaultAvatarImage(val style: String) {
    data object MysteryPerson : DefaultAvatarImage("mp")

    data object Status404 : DefaultAvatarImage("404")

    data object Identicon : DefaultAvatarImage("identicon")

    data object Monster : DefaultAvatarImage("monsterid")

    data object Wavatar : DefaultAvatarImage("wavatar")

    data object Retro : DefaultAvatarImage("retro")

    data object Blank : DefaultAvatarImage("blank")

    data object Robohash : DefaultAvatarImage("robohash")

    /**
     * @param defaultImageUrl the custom url to use as the default avatar image.
     * Rating and size parameters are ignored when the custom default is set.
     */
    data class CustomUrl(val defaultImageUrl: String) : DefaultAvatarImage("custom_url")
}
