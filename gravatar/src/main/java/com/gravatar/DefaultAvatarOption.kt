package com.gravatar

/**
 * The default avatar image to use when the user does not have a Gravatar image.
 */
public sealed class DefaultAvatarOption {
    /**
     * @suppress
     * @property style the style of the default avatar image
     */
    public abstract class Predefined(public val style: String) : DefaultAvatarOption()

    /**
     * Mystery Person: simple, cartoon-style silhouetted outline of a person (does not vary by email)
     */
    public data object MysteryPerson : Predefined("mp")

    /**
     * 404: Fallback to a 404 error instead of returning a default image. This allows to detect if the user doesn't
     * have a Gravatar image
     */
    public data object Status404 : Predefined("404")

    /**
     * Identicon: a geometric pattern based on an email hash
     */
    public data object Identicon : Predefined("identicon")

    /**
     * Monster: a generated "monster" with different colors, faces, etc
     */
    public data object MonsterId : Predefined("monsterid")

    /**
     * Wavatar: generated faces with differing features and backgrounds
     */
    public data object Wavatar : Predefined("wavatar")

    /**
     * Retro: awesome generated, 8-bit arcade-style pixelated faces
     */
    public data object Retro : Predefined("retro")

    /**
     * Blank: a transparent PNG image
     */
    public data object TransparentPNG : Predefined("blank")

    /**
     * Robohash: a generated robot with different colors, faces, etc
     */
    public data object RoboHash : Predefined("robohash")

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
     * @property defaultImageUrl the custom url to use as the default avatar image.
     */
    public data class CustomUrl(val defaultImageUrl: String) : DefaultAvatarOption()

    /**
     * Get the query parameter for the default avatar image depending on the type of default avatar image.
     *
     * @return the query parameter
     */
    public fun queryParam(): String = when (this) {
        is Predefined -> style
        is CustomUrl -> defaultImageUrl
    }
}
