package com.gravatar.quickeditor.ui.oauth

import java.util.Objects

/**
 * This class holds all the information required to launch the OAuth flow.
 *
 * @property clientId The clientId of your WP.com application
 * @property clientSecret The client secret of your WP.com application
 * @property redirectUri The redirect URI configured for your WP.com application
 */
public class OAuthParams private constructor(
    public val clientId: String,
    public val clientSecret: String,
    public val redirectUri: String,
) {
    override fun toString(): String =
        "OAuthParams(clientId=$clientId, clientSecret=$clientSecret, redirectUri=$redirectUri)"

    override fun equals(other: Any?): Boolean {
        return other is OAuthParams &&
            other.clientId == clientId &&
            other.clientSecret == clientSecret &&
            other.redirectUri == redirectUri
    }

    override fun hashCode(): Int {
        return Objects.hash(clientId, clientSecret, redirectUri)
    }

    /**
     * A type-safe builder for the OAuthParams class.
     */
    public class Builder {
        /**
         *  The clientId of your WP.com application
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var clientId: String? = null

        /**
         * The client secret of your WP.com application
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var clientSecret: String? = null

        /**
         * The redirect URI configured for your WP.com application
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var redirectUri: String? = null

        /**
         * Sets the clientId of your WP.com application
         */
        public fun setClientId(clientId: String): Builder = apply { this.clientId = clientId }

        /**
         * Sets the client secret of your WP.com application
         */
        public fun setClientSecret(clientSecret: String): Builder = apply { this.clientSecret = clientSecret }

        /**
         * Sets the redirect URI configured for your WP.com application
         */
        public fun setRedirectUri(redirectUri: String): Builder = apply { this.redirectUri = redirectUri }

        /**
         * Builds the OAuthParams object
         */
        public fun build(): OAuthParams = OAuthParams(clientId!!, clientSecret!!, redirectUri!!)
    }
}

/**
 * A type-safe builder for the OAuthParams class.
 *
 * @param initializer Function literal with OAuthParams.Builder as the receiver
 */
@JvmSynthetic // Hide from Java callers who should use Builder.
public fun OAuthParams(
    initializer: OAuthParams.Builder.() -> Unit,
): OAuthParams = OAuthParams.Builder().apply(initializer).build()
