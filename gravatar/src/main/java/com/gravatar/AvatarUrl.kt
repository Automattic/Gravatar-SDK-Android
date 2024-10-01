package com.gravatar

import com.gravatar.GravatarConstants.GRAVATAR_WWW_BASE_HOST
import com.gravatar.types.Email
import com.gravatar.types.Hash
import java.net.URL
import java.util.Locale

/**
 * Gravatar avatar URL.
 *
 * [canonicalUrl] Canonical URL of the avatar
 * [hash] Gravatar hash
 * [avatarQueryOptions] Avatar query options
 */
public class AvatarUrl {
    public val canonicalUrl: URL
    public val hash: Hash
    public var avatarQueryOptions: AvatarQueryOptions? = null

    public companion object {
        internal fun hashFromUrl(url: URL): Hash {
            val hashPart = url.path?.substringAfterLast('/')
            require(!hashPart.isNullOrEmpty()) { "Invalid Gravatar URL: $url" }
            return Hash(hashPart)
        }

        internal fun dropQueryParams(url: URL): URL {
            // Only keep the protocol, host and path
            return URL(url.protocol, url.host, url.path)
        }
    }

    /**
     * Create an avatar URL from a Gravatar hash.
     *
     * @param hash Gravatar hash
     * @param avatarQueryOptions Avatar query options
     */
    public constructor(hash: Hash, avatarQueryOptions: AvatarQueryOptions? = null) {
        this.hash = hash
        this.avatarQueryOptions = avatarQueryOptions
        this.canonicalUrl = URL("https", GRAVATAR_WWW_BASE_HOST, "/avatar/$hash")
    }

    /**
     * Create an avatar URL from an email address.
     *
     * @param email Email address
     * @param avatarQueryOptions Avatar query options
     */
    public constructor(
        email: Email,
        avatarQueryOptions: AvatarQueryOptions? = null,
    ) : this(email.hash(), avatarQueryOptions)

    /**
     * Create an avatar URL from an existing Gravatar URL.
     *
     * @param url Gravatar URL
     * @param avatarQueryOptions Avatar query options
     */
    public constructor(url: URL, avatarQueryOptions: AvatarQueryOptions? = null) {
        this.hash = hashFromUrl(url)
        // Force the removal of query parameters as we can't be sure they are valid and won't interfere with
        // the new query parameters
        this.canonicalUrl = dropQueryParams(url)
        this.avatarQueryOptions = avatarQueryOptions
        require(isAvatarUrl())
    }

    /**
     * Check if the URL is a Gravatar URL.
     *
     * @return true if the URL is a Gravatar URL
     */
    public fun isAvatarUrl(): Boolean {
        return canonicalUrl.host?.lowercase(Locale.getDefault())?.let {
            it.endsWith(".gravatar.com") || it == "gravatar.com"
        } ?: false
    }

    /**
     * Get the [URL] for the avatar.
     *
     * @return [URL] for the avatar
     */
    public fun url(): URL {
        return URL(
            canonicalUrl.protocol,
            canonicalUrl.host,
            canonicalUrl.path.plus(avatarQueryOptions.asQueryParameters()),
        )
    }
}
