package com.gravatar.di.container

import com.gravatar.BuildConfig
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

        private val WHITESPACE = Regex("\\s+")
        private const val SDK_PRODUCT = "GravatarAndroid"
    }

    internal val moshi = Moshi.Builder()
        .add(URIJsonAdapter())
        .build()

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null
    var appName: String? = null
    var appVersion: String? = null

    /**
     * The User-Agent sent with every request, e.g. `wp-android/27.6 GravatarAndroid/2.5.0`.
     *
     * Without an app identifier this is just the SDK token, which still beats OkHttp's default
     * `okhttp/<version>` — that one makes SDK traffic indistinguishable from any other client and
     * leaves nothing for a server to attribute a request to. Whitespace in [appName] is replaced
     * because a User-Agent product token cannot contain any.
     */
    val userAgent: String
        get() = buildString {
            appName?.let { name ->
                append(name.trim().replace(WHITESPACE, "-"))
                appVersion?.let { append("/").append(it) }
                append(" ")
            }
            append(SDK_PRODUCT).append("/").append(BuildConfig.SDK_VERSION)
        }

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
