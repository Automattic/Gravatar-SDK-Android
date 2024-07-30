package com.gravatar.quickeditor.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.gravatar.quickeditor.ui.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TokenStorageTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule: CoroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var tokenStorage: TokenStorage

    private val key = "key"
    private val token = "new_token"

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("test_preferences") },
        )
        tokenStorage = TokenStorage(
            dataStore = dataStore,
            dispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        runBlocking {
            dataStore.edit { it.clear() }
        }
    }

    @Test
    fun `given token when saveToken then token is saved`() = runTest {
        tokenStorage.storeToken(key, token)

        assertEquals(token, tokenStorage.getToken(key))
    }

    @Test
    fun `given key to existing token when deleteToken then token removed`() = runTest {
        tokenStorage.storeToken(key, token)

        tokenStorage.deleteToken(key)

        assertEquals(null, tokenStorage.getToken(key))
    }
}
