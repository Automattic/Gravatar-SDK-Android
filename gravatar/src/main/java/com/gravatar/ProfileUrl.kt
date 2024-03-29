package com.gravatar

import android.net.Uri
import com.gravatar.types.Email
import com.gravatar.types.Hash

public class ProfileUrl(public val hash: Hash) {
    public val url: Uri = Uri.Builder()
        .scheme("https")
        .authority(GravatarConstants.GRAVATAR_BASE_HOST)
        .appendPath(hash.toString())
        .build()

    public constructor(email: Email) : this(email.hash())

    public fun avatarUrl(): AvatarUrl {
        return AvatarUrl(hash)
    }
}
