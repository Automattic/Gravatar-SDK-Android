package com.gravatar.quickeditor

import com.google.gson.GsonBuilder
import com.gravatar.quickeditor.data.service.WordPressOAuthApi
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class QuickEditorContainer private constructor() {
    companion object {
        val instance: QuickEditorContainer by lazy {
            QuickEditorContainer()
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
