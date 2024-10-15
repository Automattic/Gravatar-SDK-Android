package com.gravatar.services

import retrofit2.Response

/**
 * Exception thrown when an HTTP error occurs in a non-cathing method.
 *
 * [rawErrorBody] The raw error body of the response.
 * [code] The HTTP status code.
 */
internal class HttpException internal constructor(response: Response<*>) : RuntimeException() {
    val rawErrorBody: String? = response.errorBody()?.string()
    val code: Int = response.code()
}
