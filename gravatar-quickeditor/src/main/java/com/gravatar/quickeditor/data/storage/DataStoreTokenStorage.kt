package com.gravatar.quickeditor.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal class DataStoreTokenStorage(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher,
) : TokenStorage {
    override suspend fun storeToken(key: String, token: String): Unit = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = token
        }
    }

    @Suppress("SwallowedException")
    override suspend fun getToken(key: String): String? = withContext(dispatcher) {
        try {
            dataStore.data.first()[stringPreferencesKey(key)]
        } catch (exception: IOException) {
            null
        }
    }

    override suspend fun deleteToken(key: String): Unit = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
}
