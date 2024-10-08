package com.gravatar.di.container

import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V3
import com.gravatar.moshiadapers.URIJsonAdapter
import com.gravatar.services.GravatarApi
import com.gravatar.services.interceptors.AuthenticationInterceptor
import com.gravatar.services.interceptors.AvatarUploadTimeoutInterceptor
import com.gravatar.services.interceptors.SdkVersionInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    internal val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(URIJsonAdapter())
        .build()

    private fun getRetrofitApiV3Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V3)

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null
    var appName: String? = null

    fun getGravatarV3Service(okHttpClient: OkHttpClient? = null, oauthToken: String? = null): GravatarApi {
        return getRetrofitApiV3Builder().apply {
            client(okHttpClient.buildOkHttpClient(oauthToken))
        }.addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(GravatarApi::class.java)
    }

    private fun OkHttpClient?.buildOkHttpClient(oauthToken: String?) = (this ?: OkHttpClient())
        .newBuilder()
        .addInterceptor(AuthenticationInterceptor(oauthToken))
        .addInterceptor(AvatarUploadTimeoutInterceptor())
        .addInterceptor(SdkVersionInterceptor())
        .build()
}
