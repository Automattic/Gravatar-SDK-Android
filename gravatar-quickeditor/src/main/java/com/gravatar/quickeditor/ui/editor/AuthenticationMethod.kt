package com.gravatar.quickeditor.ui.editor

import com.gravatar.quickeditor.ui.oauth.OAuthParams
import java.util.Objects

/**
 * Represents the authentication method used for the Gravatar Quick Editor.
 */
public sealed class AuthenticationMethod {
    /**
     * OAuth authentication method.
     *
     * @property oAuthParams The OAuth parameters.
     */
    public class OAuth(public val oAuthParams: OAuthParams) : AuthenticationMethod() {
        override fun hashCode(): Int = Objects.hash(oAuthParams)

        override fun equals(other: Any?): Boolean = other is OAuth && other.oAuthParams == oAuthParams

        override fun toString(): String = "Authentication.OAuth(oAuthParams=$oAuthParams)"
    }

    /**
     * Bearer authentication method. If the token is invalid or expired, the user will be
     * presented with the error state and an option to close the Quick Editor.
     *
     * @property token The bearer token.
     */
    public class Bearer(public val token: String) : AuthenticationMethod() {
        override fun hashCode(): Int = Objects.hash(token)

        override fun equals(other: Any?): Boolean = other is Bearer && other.token == token

        override fun toString(): String = "Authentication.Bearer(token=$token)"
    }
}
