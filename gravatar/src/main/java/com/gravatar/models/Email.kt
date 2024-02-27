package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Email(
    @SerializedName("primary") val primary: Boolean? = null,
    @SerializedName("value") val value: String? = null,
)
