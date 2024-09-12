package com.gravatar.quickeditor.ui.oauth

import android.net.Uri
import com.gravatar.types.Email

internal object WordPressOauth {
    fun buildUrl(clientId: String, redirectUri: String, email: Email): String {
        return Uri.Builder()
            .scheme("https")
            .authority("public-api.wordpress.com")
            .appendPath("oauth2")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope[1]", "auth")
            .appendQueryParameter("scope[2]", "gravatar-profile:read")
            .appendQueryParameter("scope[3]", "gravatar-profile:manage")
            .appendQueryParameter("user_email", email.toString())
            .build()
            .toString()
    }
}
