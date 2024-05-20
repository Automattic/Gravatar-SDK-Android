package com.gravatar.services

import kotlinx.coroutines.CancellationException

internal inline fun <T> runCatchingService(block: () -> Result<T, ErrorType>): Result<T, ErrorType> {
    @Suppress("TooGenericExceptionCaught")
    return try {
        block()
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (ex: Exception) {
        Result.Failure(ex.error())
    }
}
