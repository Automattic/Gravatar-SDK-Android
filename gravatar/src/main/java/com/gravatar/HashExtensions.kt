package com.gravatar

import java.security.MessageDigest

/**
 * Convert a byte array to a hexadecimal string.
 */
private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

/**
 * Hash a string using SHA-256.
 *
 * @return SHA-256 hash as a hexadecimal string
 */
private fun String.sha256Hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHex()
}

/**
 * Hash a string using default Gravatar hashing algorithm
 *
 * @return Gravatar hash as a hexadecimal string
 */
public fun String.gravatarHash(): String {
    return this.sha256Hash()
}

/**
 * Trim, lowercase, and hash a string using the default Gravatar hashing algorithm.
 *
 * @return Gravatar hash as a hexadecimal string
 */
public fun String.trimAndGravatarHash(): String {
    return this.trim().lowercase().sha256Hash()
}
