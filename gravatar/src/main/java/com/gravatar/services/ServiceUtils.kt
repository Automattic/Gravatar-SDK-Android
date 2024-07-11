package com.gravatar.services

import kotlinx.coroutines.CancellationException
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

@Deprecated(
    "This method is deprecated and should be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.services.runCatchingRequest"),
    level = DeprecationLevel.WARNING,
)
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

internal inline fun <T> runCatchingRequest(block: () -> T): Result<T, ErrorType> {
    @Suppress("TooGenericExceptionCaught")
    return try {
        return Result.Success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (ex: Exception) {
        Result.Failure(ex.errorType())
    }
}

internal val authenticationBearer: String?
    get() = GravatarSdkDI.apiKey?.let { "Bearer $it" }
