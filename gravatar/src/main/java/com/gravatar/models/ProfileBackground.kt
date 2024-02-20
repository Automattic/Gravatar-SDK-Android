package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class ProfileBackground(
    @SerializedName("url") val url: String? = null,
    @SerializedName("color") val color: String? = null,
)
