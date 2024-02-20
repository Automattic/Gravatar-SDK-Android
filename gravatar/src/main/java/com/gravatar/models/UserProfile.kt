package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class UserProfile(
    @SerializedName("hash") val hash: String? = null,
    @SerializedName("requestHash") val requestHash: String? = null,
    @SerializedName("profileUrl") val profileUrl: String? = null,
    @SerializedName("preferredUsername") val preferredUsername: String? = null,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("photos") val photos: ArrayList<Photo> = arrayListOf(),
    @SerializedName("last_profile_edit") val lastProfileEdit: String? = null,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("aboutMe") val aboutMe: String? = null,
    @SerializedName("accounts") val accounts: ArrayList<Account> = arrayListOf(),
    @SerializedName("urls") val urls: ArrayList<String> = arrayListOf(),
    @SerializedName("share_flags") val shareFlags: ShareFlags? = ShareFlags(),
)
