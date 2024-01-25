package com.gravatar

import android.net.Uri
import com.gravatar.GravatarConstants.GRAVATAR_IMAGE_HOST
import com.gravatar.GravatarConstants.GRAVATAR_IMAGE_PATH
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
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
): String {
    return emailAddressToGravatarUri(email, size, defaultAvatarImage).toString()
}

fun emailAddressToGravatarUri(email: String, size: Int? = null, defaultAvatarImage: DefaultAvatarImage? = null): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority(GRAVATAR_IMAGE_HOST)
        .appendPath(GRAVATAR_IMAGE_PATH)
        .appendPath(emailAddressToGravatarHash(email))
        .apply {
            defaultAvatarImage?.let { appendQueryParameter("d", it.style) }
            size?.let { appendQueryParameter("size", it.toString()) }
        }.build()
}
