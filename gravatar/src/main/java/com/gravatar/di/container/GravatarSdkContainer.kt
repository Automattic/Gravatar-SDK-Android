package com.gravatar.di.container

import com.gravatar.GravatarApiService
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    private fun getRetrofitBuilder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL)

    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    fun getGravatarApiService(okHttpClient: OkHttpClient? = null): GravatarApiService {
        return getRetrofitBuilder().apply {
            okHttpClient?.let { client(it) }
        }.build().create(GravatarApiService::class.java)
    }
}
