package com.gravatar.services

import com.gravatar.di.container.GravatarSdkContainer
import kotlinx.coroutines.CancellationException

internal inline fun <T> runCatchingRequest(block: () -> T?): GravatarResult<T, ErrorType> {
    @Suppress("TooGenericExceptionCaught")
    return try {
        val result = block()
        if (result != null) {
            GravatarResult.Success(result)
        } else {
            GravatarResult.Failure(ErrorType.NotFound)
        }
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (gravatarException: GravatarException) {
        GravatarResult.Failure(gravatarException.errorType)
    } catch (ex: Exception) {
        GravatarResult.Failure(ex.errorType(GravatarSdkContainer.instance.moshi))
    }
}

@Suppress("ThrowsCount")
internal inline fun <T> runThrowingExceptionRequest(block: () -> T?): T {
    @Suppress("TooGenericExceptionCaught")
    try {
        return block() ?: throw GravatarException(ErrorType.NotFound)
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (ex: Exception) {
        throw GravatarException(ex.errorType(GravatarSdkContainer.instance.moshi), ex)
    }
}
