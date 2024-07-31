package com.gravatar.di.container

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.gravatar.GravatarApiService
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V1
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V3
import com.gravatar.services.AuthenticationInterceptor
import com.gravatar.services.GravatarApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.Instant

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    private fun getRetrofitApiV1Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V1)

    private fun getRetrofitApiV3Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V3)

    private val gson = GsonBuilder().setLenient()
        .registerTypeAdapter(
            Instant::class.java,
            JsonDeserializer { json: JsonElement, _: Type, _: JsonDeserializationContext ->
                Instant.parse(json.asString) // Parses date-time strings as ISO 8601 - "2021-08-31T00:00:00Z"
            },
        )
        .create()

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null

    /**
     * Get Gravatar API service
     *
     * @param okHttpClient The OkHttp client to use
     * @return The Gravatar API service
     */
    fun getGravatarV1Service(okHttpClient: OkHttpClient? = null): GravatarApi {
        return getRetrofitApiV1Builder().apply {
            okHttpClient?.let { client(it) }
        }.build().create(GravatarApi::class.java)
    }

    @Deprecated("Use getGravatarV3Service instead", level = DeprecationLevel.WARNING)
    fun getGravatarApiV3Service(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitApiV3Builder().apply {
            client((okHttpClient ?: OkHttpClient()).newBuilder().addInterceptor(AuthenticationInterceptor()).build())
        }.addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GravatarApiService::class.java)
    }

    fun getGravatarV3Service(okHttpClient: OkHttpClient? = null, oauthToken: String? = null): GravatarApi {
        return getRetrofitApiV3Builder().apply {
            client(
                (okHttpClient ?: OkHttpClient()).newBuilder().addInterceptor(
                    AuthenticationInterceptor(oauthToken),
                ).build(),
            )
        }.addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GravatarApi::class.java)
    }
}
