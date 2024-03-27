package com.gravatar

import com.gravatar.types.Email
import com.gravatar.types.Hash
import java.net.URL

public class ProfileUrl(public val hash: Hash) {
    public val url: URL = URL("https", GravatarConstants.GRAVATAR_BASE_HOST, hash.toString())

    public constructor(email: Email) : this(email.hash())

    public fun avatarUrl(): AvatarUrl {
        return AvatarUrl(hash)
    }
}
