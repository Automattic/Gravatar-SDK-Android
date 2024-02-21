package com.gravatar

import android.net.Uri
import com.gravatar.GravatarConstants.GRAVATAR_BASE_HOST
import com.gravatar.GravatarConstants.GRAVATAR_IMAGE_PATH
import com.gravatar.GravatarConstants.GRAVATAR_WWW_BASE_HOST
import java.security.MessageDigest

/**
 * Convert a byte array to a hexadecimal string.
 */
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
        defaultAvatarImage?.let { appendQueryParameter("d", it.queryParam()) } // eg. default monster, "d=monsterid"
        size?.let { appendQueryParameter("s", it.toString()) } // eg. size 42, "s=42"
        rating?.let { appendQueryParameter("r", it.rating) } // eg. rated pg, "r=pg"
        forceDefaultAvatarImage?.let { appendQueryParameter("f", "y") } // eg. force yes, "f=y"
    }
}

/**
 * Hash a string using SHA-256.
 *
 * @return SHA-256 hash as a hexadecimal string
 */
public fun String.sha256Hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHex()
}

/**
 * Generate a Gravatar hash the for a given email address.
 *
 * @param email Email address
 *
 * @return hash that can used to address Gravatar images or profiles
 */
public fun emailAddressToGravatarHash(email: String): String {
    return email.trim().lowercase().sha256Hash()
}

/**
 * Generate Gravatar URL for the given email address.
 *
 * @param email Email address
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar URL
 */
public fun emailAddressToGravatarUrl(
    email: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): String {
    return emailAddressToGravatarUri(email, size, defaultAvatarImage, rating, forceDefaultAvatarImage).toString()
}

/**
 * Generate Gravatar Uri for the given email address.
 *
 * @param email Email address
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar Uri
 */
public fun emailAddressToGravatarUri(
    email: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority(GRAVATAR_WWW_BASE_HOST)
        .appendPath(GRAVATAR_IMAGE_PATH)
        .appendPath(emailAddressToGravatarHash(email))
        .appendGravatarQueryParameters(size, defaultAvatarImage, rating, forceDefaultAvatarImage)
        .build()
}

/**
 * Rewrite Gravatar URL to use different options. Keep only the path and hash.
 *
 * @param url Gravatar URL
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar URL with updated query parameters
 */
public fun rewriteGravatarImageUrlQueryParams(
    url: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): String {
    return rewriteGravatarImageUriQueryParams(url, size, defaultAvatarImage, rating, forceDefaultAvatarImage).toString()
}

/**
 * Rewrite Gravatar URL to use different options. Keep only the path and hash.
 *
 * @param url Gravatar URL
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar Uri with updated query parameters
 */
public fun rewriteGravatarImageUriQueryParams(
    url: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri {
    val uri = Uri.parse(url)
    require(uri.host?.contains(GRAVATAR_BASE_HOST, true) ?: false) { "Not a Gravatar URL: ${uri.host}" }
    return Uri.Builder()
        .scheme(uri.scheme)
        .authority(uri.host)
        .appendEncodedPath(uri.pathSegments.joinToString("/"))
        .appendGravatarQueryParameters(size, defaultAvatarImage, rating, forceDefaultAvatarImage)
        .build()
}

/**
 * Get a Gravatar Url from a valid Gravatar hash.
 *
 * @param url Gravatar URL
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar Uri with updated query parameters
 */
public fun gravatarUrl(
    hash: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): String {
    return gravatarUri(hash, size, defaultAvatarImage, rating, forceDefaultAvatarImage).toString()
}

/**
 * Get a Gravatar Uri from a valid Gravatar hash.
 *
 * @param url Gravatar URL
 * @param size Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @param defaultAvatarImage Default avatar image. Optional: default to Gravatar logo
 * @param rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @param forceDefaultAvatarImage Force default avatar image. Optional: default to false
 *
 * @return Gravatar Uri with updated query parameters
 */
public fun gravatarUri(
    hash: String,
    size: Int? = null,
    defaultAvatarImage: DefaultAvatarImage? = null,
    rating: ImageRating? = null,
    forceDefaultAvatarImage: Boolean? = null,
): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority(GRAVATAR_WWW_BASE_HOST)
        .appendPath(GRAVATAR_IMAGE_PATH)
        .appendPath(hash)
        .appendGravatarQueryParameters(size, defaultAvatarImage, rating, forceDefaultAvatarImage)
        .build()
}
