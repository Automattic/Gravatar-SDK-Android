package com.gravatar.quickeditor.data.models

import com.google.gson.annotations.SerializedName

internal data class WordPressOAuthToken(
    @SerializedName("access_token") val token: String,
    @SerializedName("token_type") val type: String,
)
