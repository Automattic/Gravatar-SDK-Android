package com.gravatar.quickeditor.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal class TokenStorage(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun storeToken(key: String, token: String) = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = token
        }
    }

    @Suppress("SwallowedException")
    suspend fun getToken(key: String): String? = withContext(dispatcher) {
        try {
            dataStore.data.first()[stringPreferencesKey(key)]
        } catch (exception: IOException) {
            null
        }
    }

    suspend fun deleteToken(key: String) = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
}
