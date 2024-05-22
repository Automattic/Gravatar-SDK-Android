package com.gravatar.services

import kotlinx.coroutines.CancellationException
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

internal inline fun <T> runCatchingService(block: () -> Result<T, ErrorType>): Result<T, ErrorType> {
    @Suppress("TooGenericExceptionCaught")
    return try {
        block()
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (ex: Exception) {
        Result.Failure(ex.errorType())
    }
}

internal val authenticationBearer: String?
    get() = GravatarSdkDI.apiKey?.let { "Bearer $it" }
