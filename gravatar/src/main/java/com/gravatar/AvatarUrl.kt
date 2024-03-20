package com.gravatar

import android.net.Uri
import com.gravatar.GravatarConstants.GRAVATAR_WWW_BASE_HOST
import com.gravatar.types.Email
import com.gravatar.types.Hash
import java.util.Locale

/**
 * Gravatar avatar URL.
 */
public class AvatarUrl {
    public val canonicalUrl: Uri
    public val hash: Hash

    public companion object {
        internal fun hashFromUrl(url: Uri): Hash {
            return Hash(url.pathSegments.last())
        }

        internal fun dropQueryParams(uri: Uri): Uri {
            return Uri.Builder()
                .scheme(uri.scheme)
                .authority(uri.host)
                .appendEncodedPath(uri.pathSegments.joinToString("/"))
                .build()
        }
    }

    private fun Uri.Builder.appendGravatarQueryParameters(avatarQueryOptions: AvatarQueryOptions?): Uri.Builder {
        return this.apply {
            avatarQueryOptions?.defaultAvatarOption?.let {
                appendQueryParameter("d", it.queryParam())
            } // eg. default monster, "d=monsterid"
            avatarQueryOptions?.preferredSize?.let {
                appendQueryParameter("s", it.toString())
            } // eg. size 42, "s=42"
            avatarQueryOptions?.rating?.let {
                appendQueryParameter("r", it.rating)
            } // eg. rated pg, "r=pg"
            avatarQueryOptions?.forceDefaultAvatar?.let {
                appendQueryParameter("f", if (it) "y" else "n")
            } // eg. force yes, "f=y"
        }
    }

    /**
     * Create an avatar URL from a Gravatar hash.
     *
     * @param hash Gravatar hash
     */
    public constructor(hash: Hash) {
        this.hash = hash
        this.canonicalUrl = Uri.Builder()
            .scheme("https")
            .authority(GRAVATAR_WWW_BASE_HOST)
            .appendPath("avatar")
            .appendPath(hash.toString())
            .build()
    }

    /**
     * Create an avatar URL from an email address.
     *
     * @param email Email address
     */
    public constructor(email: Email) : this(email.hash())

    /**
     * Create an avatar URL from an existing Gravatar URL.
     *
     * @param uri Gravatar URL
     */
    public constructor(uri: Uri) {
        this.hash = hashFromUrl(uri)
        this.canonicalUrl = dropQueryParams(uri)
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
     * Get the Gravatar URL with the correct query parameters set, depending the passed options.
     *
     * @param options Avatar query options
     *
     * @return Gravatar avatar URL
     */
    public fun uri(options: AvatarQueryOptions? = null): Uri {
        return canonicalUrl.buildUpon().appendGravatarQueryParameters(options).build()
    }
}
