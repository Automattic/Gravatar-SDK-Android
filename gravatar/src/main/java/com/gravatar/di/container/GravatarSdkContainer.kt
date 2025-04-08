package com.gravatar.di.container

import com.gravatar.moshiadapers.URIJsonAdapter
import com.gravatar.restapi.apis.AvatarsApi
import com.gravatar.restapi.apis.ProfilesApi
import com.gravatar.services.interceptors.AuthenticationInterceptor
import com.gravatar.services.interceptors.AvatarUploadTimeoutInterceptor
import com.gravatar.services.interceptors.SdkVersionInterceptor
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    internal val moshi = Moshi.Builder()
        .add(URIJsonAdapter())
        .build()

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null
    var appName: String? = null

    fun getProfilesApi(okHttpClient: OkHttpClient? = null, oauthToken: String? = null): ProfilesApi =
        ProfilesApi(client = okHttpClient.buildOkHttpClient(oauthToken))

    fun getAvatarsApi(okHttpClient: OkHttpClient?, oauthToken: String?): AvatarsApi =
        AvatarsApi(client = okHttpClient.buildOkHttpClient(oauthToken))

    private fun OkHttpClient?.buildOkHttpClient(oauthToken: String?) = (this ?: OkHttpClient())
        .newBuilder()
        .addInterceptor(AuthenticationInterceptor(oauthToken))
        .addInterceptor(AvatarUploadTimeoutInterceptor())
        .addInterceptor(SdkVersionInterceptor())
        .build()
}
