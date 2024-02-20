package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Photo(
    @SerializedName("value") val value: String? = null,
    @SerializedName("type") val type: String? = null,
)
