package com.gravatar.services

import com.gravatar.HttpResponseCode
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun errorTypeFromHttpCode(code: Int): ErrorType = when (code) {
    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.TIMEOUT
    HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NOT_FOUND
    HttpResponseCode.HTTP_TOO_MANY_REQUESTS -> ErrorType.RATE_LIMIT_EXCEEDED
    in HttpResponseCode.SERVER_ERRORS -> ErrorType.SERVER
    else -> ErrorType.UNKNOWN
}

internal fun Throwable.errorType(): ErrorType {
    return when (this) {
        is SocketTimeoutException -> ErrorType.TIMEOUT
        is UnknownHostException -> ErrorType.NETWORK
        is HttpException -> errorTypeFromHttpCode(code)
        else -> ErrorType.UNKNOWN
    }
}

/**
 * Error types for Gravatar image upload
 */
public enum class ErrorType {
    /** server returned an error */
    SERVER,

    /** network request timed out */
    TIMEOUT,

    /** network is not available */
    NETWORK,

    /** User or hash not found */
    NOT_FOUND,

    /** User or hash not found */
    RATE_LIMIT_EXCEEDED,

    /** An unknown error occurred */
    UNKNOWN,
}
