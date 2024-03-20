package com.gravatar.services

import com.gravatar.HttpResponseCode

internal fun errorTypeFromHttpCode(code: Int): ErrorType = when (code) {
    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.TIMEOUT
    HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NOT_FOUND
    in HttpResponseCode.SERVER_ERRORS -> ErrorType.SERVER
    else -> ErrorType.UNKNOWN
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

    /** An unknown error occurred */
    UNKNOWN,
}
