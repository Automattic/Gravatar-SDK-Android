package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Urls(
    @SerializedName("title") val title: String? = null,
    @SerializedName("value") val value: String? = null,
)
