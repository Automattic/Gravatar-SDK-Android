package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class ShareFlags(
    @SerializedName("search_engines") val searchEngines: Boolean? = null,
)
