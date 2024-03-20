package com.gravatar.types

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
public fun String.sha256Hash(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHex()
}

/**
 * Gravatar hash.
 */
public class Hash(private val hash: String) {
    public override fun toString(): String = this.hash
}
