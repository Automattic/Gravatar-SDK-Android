package com.gravatar.models

import com.google.gson.annotations.SerializedName

public data class Entry(
    @SerializedName("hash") val hash: String? = null,
    @SerializedName("requestHash") val requestHash: String? = null,
    @SerializedName("profileUrl") val profileUrl: String? = null,
    @SerializedName("preferredUsername") val preferredUsername: String? = null,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("photos") val photos: ArrayList<Photo> = arrayListOf(),
    @SerializedName("last_profile_edit") val lastProfileEdit: String? = null,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("pronouns") val pronouns: String? = null,
    @SerializedName("emails") val emails: ArrayList<Email> = arrayListOf(),
    @SerializedName("name") val name: Name? = Name(),
    @SerializedName("accounts") val accounts: ArrayList<Account> = arrayListOf(),
    @SerializedName("currency") val currency: ArrayList<Currency> = arrayListOf(),
    @SerializedName("urls") val urls: ArrayList<Urls> = arrayListOf(),
    @SerializedName("profileBackground") val profileBackground: ProfileBackground? = ProfileBackground(),
    @SerializedName("share_flags") val shareFlags: ShareFlags? = ShareFlags(),
)
