package com.gravatar

/**
 * The default avatar image to use when the user does not have a gravatar image.
 */
sealed class DefaultAvatarImage {
    /**
     * @suppress
     */
    abstract class Predefined(val style: String) : DefaultAvatarImage()

    /**
     * Mystery Person: simple, cartoon-style silhouetted outline of a person (does not vary by email)
     */
    data object MysteryPerson : Predefined("mp")

    /**
     * 404: Fallback to a 404 error image
     */
    data object Status404 : Predefined("404")

    /**
     * Identicon: a geometric pattern based on an email hash
     */
    data object Identicon : Predefined("identicon")

    /**
     * Monster: a generated "monster" with different colors, faces, etc
     */
    data object Monster : Predefined("monsterid")

    /**
     * Wavatar: generated faces with differing features and backgrounds
     */
    data object Wavatar : Predefined("wavatar")

    /**
     * Retro: awesome generated, 8-bit arcade-style pixelated faces
     */
    data object Retro : Predefined("retro")

    /**
     * Blank: a transparent PNG image
     */
    data object Blank : Predefined("blank")

    /**
     * Robohash: a generated robot with different colors, faces, etc
     */
    data object Robohash : Predefined("robohash")

    /**
     * If you prefer to use your own default image (perhaps your logo, a funny face, whatever), then you can
     * easily do so by using the CustomUrl option and supplying the URL to an image.
     *
     * Rating and size parameters are ignored when the custom default is set.
     * There are a few conditions which must be met for default image URL:
     *   - MUST be publicly available (e.g. cannot be on an intranet, on a local development machine,
     *          behind HTTP Auth or some other firewall etc). Default images are passed through a security
     *          scan to avoid malicious content.
     *   - MUST be accessible via HTTP or HTTPS on the standard ports, 80 and 443, respectively.
     *   - MUST have a recognizable image extension (jpg, jpeg, gif, png, heic)
     *   - MUST NOT include a querystring (if it does, it will be ignored)
     *
     * @param defaultImageUrl the custom url to use as the default avatar image.
     */
    data class CustomUrl(val defaultImageUrl: String) : DefaultAvatarImage()

    fun queryParam(): String = when (this) {
        is Predefined -> style
        is CustomUrl -> defaultImageUrl
    }
}
