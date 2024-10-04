package com.gravatar.services.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

private const val TIMEOUT_DURATION_MILLIS = 5 * 60 * 1000 // 5 minutes

/**
 * Increases the timeout for the avatar upload request
 */
internal class AvatarUploadTimeoutInterceptor(
    private val timeoutMillis: Int = TIMEOUT_DURATION_MILLIS,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newChain = if (request.method == "POST" && request.url.encodedPath == "/v3/me/avatars") {
            chain.withConnectTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .withReadTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .withWriteTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
        } else {
            chain
        }
        return newChain.proceed(chain.request().newBuilder().build())
    }
}
