package com.gravatar.services

import okhttp3.Interceptor
import okhttp3.Response
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

internal class AuthenticationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                if (GravatarSdkDI.apiKey != null) {
                    addHeader("Authorization", "Bearer " + GravatarSdkDI.apiKey)
                }
            }.build(),
        )
    }
}
