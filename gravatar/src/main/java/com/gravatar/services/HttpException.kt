package com.gravatar.services

import retrofit2.Response

/**
 * Exception thrown when an HTTP error occurs in a non-cathing method.
 *
 * [code] The HTTP status code.
 * [message] The HTTP status message.
 */
public class HttpException internal constructor(response: Response<*>) : RuntimeException() {
    public val code: Int = response.code()
    public override val message: String = "HTTP ${response.code()} ${response.errorBody()?.string()}"
}
