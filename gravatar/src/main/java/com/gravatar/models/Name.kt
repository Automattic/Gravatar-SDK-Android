package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Name(
    @SerializedName("givenName") val givenName: String? = null,
    @SerializedName("familyName") val familyName: String? = null,
    @SerializedName("formatted") val formatted: String? = null,
)
