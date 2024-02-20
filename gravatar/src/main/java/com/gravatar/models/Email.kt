package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Email(
    @SerializedName("primary") val primary: String? = null,
    @SerializedName("value") val value: String? = null,
)
