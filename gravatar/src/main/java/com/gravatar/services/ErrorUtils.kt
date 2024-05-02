package com.gravatar.services

import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun Throwable.error(): ErrorType {
    return when (this) {
        is SocketTimeoutException -> ErrorType.TIMEOUT
        is UnknownHostException -> ErrorType.NETWORK
        else -> ErrorType.UNKNOWN
    }
}
