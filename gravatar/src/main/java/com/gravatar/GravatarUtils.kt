package com.gravatar

import android.net.Uri
import com.gravatar.GravatarConstants.GRAVATAR_IMAGE_HOST
import com.gravatar.GravatarConstants.GRAVATAR_IMAGE_PATH
import java.security.MessageDigest

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

private fun Uri.Builder.appendGravatarQueryParameters(
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri.Builder {
    return this.apply {
        defaultAvatarImage?.let { appendQueryParameter("d", it.style) } // eg. default monster, "d=monsterid"
        size?.let { appendQueryParameter("s", it.toString()) } // eg. size 42, "s=42"
        rating?.let { appendQueryParameter("r", it.rating) } // eg. rated pg, "r=pg"
        forceDefaultAvatarImage?.let { appendQueryParameter("f", "y") } // eg. force yes, "f=y"
    }
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
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): String {
    return emailAddressToGravatarUri(email, size, defaultAvatarImage, rating, forceDefaultAvatarImage).toString()
}

fun emailAddressToGravatarUri(
    email: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority(GRAVATAR_IMAGE_HOST)
        .appendPath(GRAVATAR_IMAGE_PATH)
        .appendPath(emailAddressToGravatarHash(email))
        .appendGravatarQueryParameters(size, defaultAvatarImage, rating, forceDefaultAvatarImage)
        .build()
}

fun gravatarImageUrlToGravatarImageUrl(
    url: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): String {
    return gravatarImageUrlToGravatarImageUri(url, size, defaultAvatarImage, rating, forceDefaultAvatarImage).toString()
}

/**
 * Rewrite gravatar URL to use different options. Keep only the path and hash.
 *
 * @param url Gravatar URL
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 */
fun gravatarImageUrlToGravatarImageUri(
    url: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri {
    val uri = Uri.parse(url)
    require(uri.host?.let { GRAVATAR_IMAGE_HOST.contains(it, true) } == true) { "Not a gravatar URL" }
    return Uri.Builder()
        .scheme("https")
        .authority(GRAVATAR_IMAGE_HOST)
        .appendEncodedPath(uri.pathSegments.joinToString("/"))
        .appendGravatarQueryParameters(size, defaultAvatarImage, rating, forceDefaultAvatarImage)
        .build()
}
