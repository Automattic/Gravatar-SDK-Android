package com.gravatar.di.container

import com.google.gson.GsonBuilder
import com.gravatar.GravatarApiService
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V1
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V3
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Container that handle the DI for the Gravatar SDK.
 */
public class GravatarSdkContainer private constructor() {
    public companion object {
        /**
         * The singleton instance of the Gravatar SDK container.
         */
        public val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    private fun getRetrofitApiV1Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V1)

    private fun getRetrofitApiV3Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V3)

    /**
     * The main dispatcher for the SDK.
     */
    public val dispatcherMain: CoroutineDispatcher = Dispatchers.Main

    /**
     * The default dispatcher for the SDK.
     */
    public val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default

    /**
     * The IO dispatcher for the SDK.
     */
    public val dispatcherIO: CoroutineDispatcher = Dispatchers.IO

    private val gson = GsonBuilder().setLenient().create()

    /**
     * Get Gravatar API service
     *
     * @param okHttpClient The OkHttp client to use
     * @return The Gravatar API service
     */
    internal fun getGravatarApiV1Service(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitApiV1Builder().apply {
            okHttpClient?.let { client(it) }
        }.build().create(GravatarApiService::class.java)
    }

    internal fun getGravatarApiV3Service(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitApiV3Builder().apply {
            okHttpClient?.let { client(it) }
        }.addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GravatarApiService::class.java)
    }
}
