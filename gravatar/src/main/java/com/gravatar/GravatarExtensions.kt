package com.gravatar

import android.net.Uri
import java.security.MessageDigest

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.sha256Hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHex()
}

fun String.emailAddressToGravatarHash(): String {
    return this.trim().lowercase().sha256Hash()
}

fun String.gravatarUrl(
    email: String,
    defaultAvatarImage: DefaultAvatarImage? = null,
    size: Int? = null,
): String {
    return Uri.Builder()
        .scheme("https")
        .authority("www.gravatar.com")
        .appendPath("avatar")
        .appendPath(email.emailAddressToGravatarHash())
        .apply {
            defaultAvatarImage?.let { appendQueryParameter("d", it.style) }
            size?.let { appendQueryParameter("size", it.toString()) }
        }.build().toString()
}
