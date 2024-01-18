package com.gravatar

import android.net.Uri
import java.security.MessageDigest

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.sha256Hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHex()
}

fun emailAddressToGravatarHash(email: String): String {
    return email.trim().lowercase().sha256Hash()
}

fun emailAddressToGravatarUrl(
    email: String,
    defaultAvatarImage: DefaultAvatarImage? = null,
    size: Int? = null,
): String {
    return emailAddressToGravatarUri(email, defaultAvatarImage, size).toString()
}

fun emailAddressToGravatarUri(
    email: String,
    defaultAvatarImage: DefaultAvatarImage? = null,
    size: Int? = null,
): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority("www.gravatar.com")
        .appendPath("avatar")
        .appendPath(emailAddressToGravatarHash(email))
        .apply {
            defaultAvatarImage?.let { appendQueryParameter("d", it.style) }
            size?.let { appendQueryParameter("size", it.toString()) }
        }.build()
}
