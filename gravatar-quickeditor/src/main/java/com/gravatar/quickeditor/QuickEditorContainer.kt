package com.gravatar.quickeditor

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.google.gson.GsonBuilder
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.datastore.createEncryptedFileWithFallbackReset
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.service.WordPressOAuthApi
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.IdentityService
import com.gravatar.services.ProfileService
import io.github.osipxd.security.crypto.createEncrypted
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class QuickEditorContainer private constructor(
    private val context: Context,
) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: QuickEditorContainer

        fun init(context: Context): QuickEditorContainer {
            instance = QuickEditorContainer(context)
            return instance
        }

        fun getInstance(): QuickEditorContainer {
            check(::instance.isInitialized) {
                "QuickEditorContainer is not initialized. Call init() first."
            }
            return instance
        }
    }

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createEncrypted(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        context.createEncryptedFileWithFallbackReset(name = "quick-editor-preferences")
    }

    val tokenStorage: TokenStorage by lazy {
        TokenStorage(dataStore = dataStore, dispatcher = Dispatchers.IO)
    }

    val wordPressOAuthService: WordPressOAuthService by lazy {
        WordPressOAuthService(
            wordPressOAuthApi = getWordpressOAuthApi(),
            dispatcher = Dispatchers.IO,
        )
    }

    private val avatarService: AvatarService by lazy {
        AvatarService()
    }

    val profileService: ProfileService by lazy {
        ProfileService()
    }

    private val identityService: IdentityService by lazy {
        IdentityService()
    }

    val fileUtils: FileUtils by lazy {
        FileUtils(context)
    }

    val avatarRepository: AvatarRepository by lazy {
        AvatarRepository(
            avatarService = avatarService,
            identityService = identityService,
            tokenStorage = tokenStorage,
            dispatcher = Dispatchers.IO,
        )
    }

    private fun getWordpressOAuthApi(): WordPressOAuthApi {
        return Retrofit.Builder()
            .baseUrl("https://public-api.wordpress.com")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(WordPressOAuthApi::class.java)
    }
}
