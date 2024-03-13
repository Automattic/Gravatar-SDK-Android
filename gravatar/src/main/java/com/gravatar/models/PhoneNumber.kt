package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class PhoneNumber(
    @SerializedName("type") val title: String? = null,
    @SerializedName("value") val value: String? = null,
)
