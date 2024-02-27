package com.gravatar.di.container

import com.google.gson.GsonBuilder
import com.gravatar.GravatarApiService
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL
import com.gravatar.GravatarConstants.GRAVATAR_BASE_URL
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    private fun getRetrofitApiBuilder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL)

    private fun getRetrofitBaseBuilder() = Retrofit.Builder().baseUrl(GRAVATAR_BASE_URL)

    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO
    private val gson = GsonBuilder().setLenient().create()

    /**
     * Get Gravatar API service
     *
     * @param okHttpClient The OkHttp client to use
     * @return The Gravatar API service
     */
    fun getGravatarApiService(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitApiBuilder().apply {
            okHttpClient?.let { client(it) }
        }.build().create(GravatarApiService::class.java)
    }

    fun getGravatarBaseService(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitBaseBuilder().apply {
            okHttpClient?.let { client(it) }
        }.addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GravatarApiService::class.java)
    }
}
