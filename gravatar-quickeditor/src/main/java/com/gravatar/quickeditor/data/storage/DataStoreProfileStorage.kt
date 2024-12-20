package com.gravatar.quickeditor.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

internal class DataStoreProfileStorage(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher,
) : ProfileStorage {
    private val loginIntroShownKeyPrefix = "login_intro_shown"

    override suspend fun setLoginIntroShown(emailHash: String): Unit = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(emailHash.loginIntroShownKey)] = true
        }
    }

    @Suppress("SwallowedException")
    override suspend fun getLoginIntroShown(emailHash: String): Boolean = withContext(dispatcher) {
        try {
            dataStore.data.first()[booleanPreferencesKey(emailHash.loginIntroShownKey)] ?: false
        } catch (exception: IOException) {
            false
        }
    }

    private val String.loginIntroShownKey: String
        get() = "${loginIntroShownKeyPrefix}_$this"
}
