package com.gravatar.services

import com.gravatar.HttpResponseCode.HTTP_CLIENT_TIMEOUT
import com.gravatar.HttpResponseCode.HTTP_NOT_FOUND
import com.gravatar.HttpResponseCode.HTTP_TOO_MANY_REQUESTS
import com.gravatar.HttpResponseCode.INVALID_REQUEST
import com.gravatar.HttpResponseCode.SERVER_ERRORS
import com.gravatar.restapi.models.Error
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorTypeTest {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val errorBody = """
        {
            "error": "Only square images are accepted",
            "code": "uncropped_image"
        }
    """.trimIndent()

    private val httpCodeToErrorTypeRelation = mutableListOf(
        HTTP_CLIENT_TIMEOUT to ErrorType.Timeout,
        HTTP_NOT_FOUND to ErrorType.NotFound,
        HTTP_TOO_MANY_REQUESTS to ErrorType.RateLimitExceeded,
        600 to ErrorType.Unknown,
        INVALID_REQUEST to ErrorType.InvalidRequest(
            error = Error {
                code = "uncropped_image"
                error = "Only square images are accepted"
            },
        ),
    ).apply {
        SERVER_ERRORS.forEach { code ->
            add(code to ErrorType.Server)
        }
    }

    @Test
    fun `given an http code when converting to error type then correct type is returned`() {
        httpCodeToErrorTypeRelation.forEach { (code, expectedErrorType) ->
            val response = mockk<Response<Unit>>(relaxed = true) {
                every { code() } returns code
                every { errorBody() } returns mockk {
                    every { string() } returns errorBody
                }
            }
            // When
            val errorType = HttpException(response).errorTypeFromHttpCode(moshi)
            // Then
            assertEquals(expectedErrorType, errorType)
        }
    }

    @Test
    fun `given an exception when converting to error type then correct type is returned`() {
        // Given
        val exceptionToErrorTypeRelation = mutableListOf(
            SocketTimeoutException() to ErrorType.Timeout,
            UnknownHostException() to ErrorType.Network,
            Exception() to ErrorType.Unknown,
        ).apply {
            httpCodeToErrorTypeRelation.forEach { (code, errorType) ->
                val exception = mockk<HttpException>(relaxed = true) {
                    every { this@mockk.code } returns code
                    every { this@mockk.rawErrorBody } returns errorBody
                }
                add(exception to errorType)
            }
        }
        exceptionToErrorTypeRelation.forEach { (exception, expectedErrorType) ->
            // When
            val errorType = exception.errorType(moshi)
            // Then
            assertEquals(expectedErrorType, errorType)
        }
    }
}
