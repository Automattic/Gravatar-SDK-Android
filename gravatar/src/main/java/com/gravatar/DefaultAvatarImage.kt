package com.gravatar

sealed class DefaultAvatarImage {
    abstract class Predefined(val style: String) : DefaultAvatarImage()

    data object MysteryPerson : Predefined("mp")

    data object Status404 : Predefined("404")

    data object Identicon : Predefined("identicon")

    data object Monster : Predefined("monsterid")

    data object Wavatar : Predefined("wavatar")

    data object Retro : Predefined("retro")

    data object Blank : Predefined("blank")

    data object Robohash : Predefined("robohash")

    /**
     * @param defaultImageUrl the custom url to use as the default avatar image.
     * Rating and size parameters are ignored when the custom default is set.
     */
    data class CustomUrl(val defaultImageUrl: String) : DefaultAvatarImage()
}
}
