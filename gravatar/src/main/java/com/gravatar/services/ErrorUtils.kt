package com.gravatar.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun handleError(t: Throwable, listener: GravatarListener<*, ErrorType>, coroutineScope: CoroutineScope) {
    val error: ErrorType =
        when (t) {
            is SocketTimeoutException -> ErrorType.TIMEOUT
            is UnknownHostException -> ErrorType.NETWORK
            else -> ErrorType.UNKNOWN
        }
    coroutineScope.launch {
        listener.onError(error)
    }
}
