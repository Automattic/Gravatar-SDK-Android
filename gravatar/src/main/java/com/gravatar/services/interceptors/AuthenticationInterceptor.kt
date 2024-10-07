package com.gravatar.services.interceptors

import com.gravatar.di.container.GravatarSdkContainer
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class AuthenticationInterceptor(private val oauthToken: String? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addAuthorizationHeaderIfPresent()
                .build(),
        )
    }

    private fun Request.Builder.addAuthorizationHeaderIfPresent(): Request.Builder {
        return oauthToken?.let { addHeader(it) } ?: GravatarSdkContainer.instance.apiKey?.let { addHeader(it) } ?: this
    }

    private fun Request.Builder.addHeader(bearer: String) = addHeader("Authorization", "Bearer $bearer")
}
