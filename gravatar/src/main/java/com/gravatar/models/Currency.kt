package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Currency(
    @SerializedName("type") val type: String? = null,
    @SerializedName("value") val value: String? = null,
)
