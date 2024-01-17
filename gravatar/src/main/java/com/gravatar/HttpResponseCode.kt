package com.gravatar

object HttpResponseCode {
    // 4xx codes
    const val HTTP_CLIENT_TIMEOUT = 408

    // 5xx codes
    private const val HTTP_INTERNAL_ERROR = 500
    private const val NETWORK_CONNECT_TIMEOUT_ERROR = 599
    val SERVER_ERRORS = HTTP_INTERNAL_ERROR..NETWORK_CONNECT_TIMEOUT_ERROR
}
