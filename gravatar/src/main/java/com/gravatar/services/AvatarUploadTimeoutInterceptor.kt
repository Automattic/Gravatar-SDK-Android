package com.gravatar.services

import okhttp3.Interceptor
import okhttp3.Response
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val TIMEOUT_DURATION = 5L

/**
 * Increases the timeout for the avatar upload request
 */
internal class AvatarUploadTimeoutInterceptor(
    private val timeout: Duration = Duration.ofMinutes(TIMEOUT_DURATION),
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newChain = if (request.method == "POST" && request.url.encodedPath == "/v3/me/avatars") {
            chain.withConnectTimeout(timeout.toMillis().toInt(), TimeUnit.MILLISECONDS)
                .withReadTimeout(timeout.toMillis().toInt(), TimeUnit.MILLISECONDS)
                .withWriteTimeout(timeout.toMillis().toInt(), TimeUnit.MILLISECONDS)
        } else {
            chain
        }
        return newChain.proceed(chain.request().newBuilder().build())
    }
}
