package com.gravatar.services.interceptors

import com.gravatar.BuildConfig
import com.gravatar.di.container.GravatarSdkContainer
import okhttp3.Interceptor
import okhttp3.Response

internal class SdkVersionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                addHeader("X-Platform", "Android")
                addHeader("X-SDK-Version", BuildConfig.SDK_VERSION)
                GravatarSdkContainer.instance.appName?.let { addHeader("X-Source", it) }
            }.build(),
        )
    }
}
