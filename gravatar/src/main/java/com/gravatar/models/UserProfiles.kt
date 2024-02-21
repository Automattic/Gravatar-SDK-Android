package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class UserProfiles(
    @SerializedName("entry") val entry: ArrayList<UserProfile> = arrayListOf(),
)
