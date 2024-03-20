package com.gravatar.types

public class Email(private val address: String) {
    /**
     * Get a Gravatar hash for a given email address.
     *
     * @return hash that can used to address Gravatar images or profiles
     */
    public fun hash(): Hash {
        return Hash(address.trim().lowercase().sha256Hash())
    }

    public override fun toString(): String = this.address
}
