package com.gravatar.services

import com.gravatar.restapi.infrastructure.ApiResponse

/**
 * Exception thrown when an HTTP error occurs in a non-cathing method.
 *
 * [code] The HTTP status code.
 * [message] The HTTP status message.
 */
public class HttpException internal constructor(response: ApiResponse<*>) : RuntimeException() {
    internal val rawErrorBody: String? = response.errorBody?.string()
    public val code: Int = response.code
    public override val message: String = "HTTP $code $rawErrorBody"
}
