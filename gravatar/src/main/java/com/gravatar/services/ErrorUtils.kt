package com.gravatar.services

import com.gravatar.HttpResponseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun httpErrorToErrorType(code: Int): ErrorType = when (code) {
    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.TIMEOUT
    HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NOT_FOUND
    in HttpResponseCode.SERVER_ERRORS -> ErrorType.SERVER
    else -> ErrorType.UNKNOWN
}

internal fun handleError(t: Throwable, listener: GravatarListener<*>, coroutineScope: CoroutineScope) {
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
