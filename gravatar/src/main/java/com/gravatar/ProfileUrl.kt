package com.gravatar

import com.gravatar.types.Email
import com.gravatar.types.Hash
import java.net.URL

/**
 * Represents a Gravatar profile URL.
 *
 * @property hash The hash of the email address.
 */
public class ProfileUrl(public val hash: Hash) {
    /**
     * The URL of the profile.
     */
    public val url: URL = URL("https", GravatarConstants.GRAVATAR_BASE_HOST, hash.toString())

    public constructor(email: Email) : this(email.hash())

    /**
     * Get the [AvatarUrl] for the represented profile.
     *
     * @return the [AvatarUrl] of the avatar image
     */
    public fun avatarUrl(): AvatarUrl {
        return AvatarUrl(hash)
    }
}
