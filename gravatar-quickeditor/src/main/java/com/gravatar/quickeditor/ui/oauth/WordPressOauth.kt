package com.gravatar.quickeditor.ui.oauth

internal object WordPressOauth {
    private const val RESPONSE_TYPE = "code"
    private const val SCOPE = "auth"
    private const val URL =
        "https://public-api.wordpress.com/oauth2/authorize" +
            "?client_id=%s&redirect_uri=%s&response_type=$RESPONSE_TYPE&scope=$SCOPE"

    fun buildUrl(clientId: String, redirectUri: String) = String.format(URL, clientId, redirectUri)
}
