package com.gravatar.services.interceptors

import com.gravatar.BuildConfig
import com.gravatar.di.container.GravatarSdkContainer
import okhttp3.Interceptor
import okhttp3.Response

internal class SdkVersionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            request.newBuilder().apply {
                addHeader("X-Platform", "Android")
                addHeader("X-SDK-Version", BuildConfig.SDK_VERSION)
                GravatarSdkContainer.instance.appName?.let { addHeader("X-Source", it) }
                // Only when absent, so a caller that supplied its own client and User-Agent keeps it.
                // OkHttp's BridgeInterceptor runs after this one and also only fills a missing header,
                // so setting it here is what stops the default `okhttp/<version>` from going out.
                if (request.header(USER_AGENT) == null) {
                    header(USER_AGENT, GravatarSdkContainer.instance.userAgent)
                }
            }.build(),
        )
    }

    private companion object {
        const val USER_AGENT = "User-Agent"
    }
}
