package com.gravatar.quickeditor

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.gravatar.quickeditor.data.service.WordPressOAuthApi
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.TokenStorage
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

    val wordPressOAuthService: WordPressOAuthService by lazy {
        WordPressOAuthService(
            wordPressOAuthApi = getWordpressOAuthApi(),
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
