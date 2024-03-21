package com.gravatar

import android.net.Uri

public class ProfileUrl(public val hash: String) {
    public companion object {
        public fun fromEmail(email: String): ProfileUrl {
            return ProfileUrl(email.trimAndGravatarHash())
        }
    }

    public val url: Uri = Uri.Builder()
        .scheme("https")
        .authority(GravatarConstants.GRAVATAR_BASE_HOST)
        .appendPath(hash.toString())
        .build()

    public fun avatarUrl(): AvatarUrl {
        return AvatarUrl(hash)
    }
}
