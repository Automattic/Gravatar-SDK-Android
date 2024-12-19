package com.gravatar.quickeditor.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.gravatar.quickeditor.ui.CoroutineTestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DataStoreProfileStorageTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule: CoroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var profileStorage: DataStoreProfileStorage

    private val emailHash = "emailHash"

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("test_preferences") },
        )
        profileStorage = DataStoreProfileStorage(
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
    fun `given emailHash when setLoginIntroShown then loginIntroShown is true`() = runTest {
        profileStorage.setLoginIntroShown(emailHash)

        val key = booleanPreferencesKey("login_intro_shown_$emailHash")
        val preferences = dataStore.data.first()
        assertEquals(true, preferences[key])
    }

    @Test
    fun `given login intro shown when getLoginIntroShown then return true`() = runTest {
        val key = booleanPreferencesKey("login_intro_shown_$emailHash")
        dataStore.edit { it[key] = true }

        val result = profileStorage.getLoginIntroShown(emailHash)
        assertEquals(true, result)
    }

    @Test
    fun `given emailHash when getLoginIntroShown and key does not exist then return false`() = runTest {
        val result = profileStorage.getLoginIntroShown(emailHash)
        assertEquals(false, result)
    }
}
