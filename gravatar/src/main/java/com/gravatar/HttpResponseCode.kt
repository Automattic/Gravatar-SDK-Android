package com.gravatar

internal object HttpResponseCode {
    /** HTTP client timeout code */
    const val HTTP_CLIENT_TIMEOUT = 408

    private const val HTTP_INTERNAL_ERROR = 500
    private const val NETWORK_CONNECT_TIMEOUT_ERROR = 599

    /** Server error codes (5xx) */
    val SERVER_ERRORS = HTTP_INTERNAL_ERROR..NETWORK_CONNECT_TIMEOUT_ERROR
}
