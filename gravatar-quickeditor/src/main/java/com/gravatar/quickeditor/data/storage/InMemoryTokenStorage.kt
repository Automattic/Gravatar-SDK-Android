package com.gravatar.quickeditor.data.storage

import java.util.concurrent.ConcurrentHashMap

internal class InMemoryTokenStorage : TokenStorage {
    private val tokens = ConcurrentHashMap<String, String>()

    override suspend fun storeToken(key: String, token: String) {
        tokens.put(key, token)
    }

    override suspend fun getToken(key: String): String? {
        return tokens.get(key)
    }

    override suspend fun deleteToken(key: String) {
        tokens.remove(key)
    }
}
