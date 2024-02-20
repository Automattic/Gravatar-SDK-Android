package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Account(
    @SerializedName("domain") val domain: String? = null,
    @SerializedName("display") val display: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("iconUrl") val iconUrl: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("verified") val verified: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("shortname") val shortname: String? = null,
)
