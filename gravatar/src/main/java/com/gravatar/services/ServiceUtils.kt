package com.gravatar.services

import com.gravatar.di.container.GravatarSdkContainer
import kotlinx.coroutines.CancellationException
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

internal inline fun <T> runCatchingRequest(block: () -> T?): Result<T, ErrorType> {
    @Suppress("TooGenericExceptionCaught")
    return try {
        val result = block()
        if (result != null) {
            Result.Success(result)
        } else {
            Result.Failure(ErrorType.NotFound)
        }
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (ex: Exception) {
        Result.Failure(ex.errorType(GravatarSdkContainer.instance.gson))
    }
}

internal val authenticationBearer: String?
    get() = GravatarSdkDI.apiKey?.let { "Bearer $it" }
