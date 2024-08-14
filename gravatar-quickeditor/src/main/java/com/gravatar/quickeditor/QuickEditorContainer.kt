package com.gravatar.quickeditor

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.gravatar.quickeditor.data.service.WordPressOAuthApi
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
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

    private val Context.dataStore by preferencesDataStore(name = "quick-editor-preferences")

    val tokenStorage: TokenStorage by lazy {
        TokenStorage(dataStore = context.dataStore, dispatcher = Dispatchers.IO)
    }

    val wordPressOAuthService: WordPressOAuthService by lazy {
        WordPressOAuthService(
            wordPressOAuthApi = getWordpressOAuthApi(),
            dispatcher = Dispatchers.IO,
        )
    }

    val avatarService: AvatarService by lazy {
        AvatarService()
    }

    val profileService: ProfileService by lazy {
        ProfileService()
    }

    private fun getWordpressOAuthApi(): WordPressOAuthApi {
        return Retrofit.Builder()
            .baseUrl("https://public-api.wordpress.com")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(WordPressOAuthApi::class.java)
    }
}
