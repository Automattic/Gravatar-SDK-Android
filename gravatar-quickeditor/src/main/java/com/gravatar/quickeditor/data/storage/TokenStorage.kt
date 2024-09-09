package com.gravatar.quickeditor.data.storage

internal interface TokenStorage {
    suspend fun storeToken(key: String, token: String)

    suspend fun getToken(key: String): String?

    suspend fun deleteToken(key: String)
}
