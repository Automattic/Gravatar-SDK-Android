package com.gravatar.quickeditor.data.storage

internal interface ProfileStorage {
    suspend fun setLoginIntroShown(emailHash: String)

    suspend fun getLoginIntroShown(emailHash: String): Boolean
}
