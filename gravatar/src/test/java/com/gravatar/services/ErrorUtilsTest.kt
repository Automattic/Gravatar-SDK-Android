package com.gravatar.services

import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorUtilsTest {
    @Test
    fun `given an exception when converting to error type then correct type is returned`() {
        // Given
        val exceptionToErrorTypeRelation = mutableListOf(
            SocketTimeoutException() to ErrorType.TIMEOUT,
            UnknownHostException() to ErrorType.NETWORK,
            Exception() to ErrorType.UNKNOWN,
        )
        exceptionToErrorTypeRelation.forEach { (exception, expectedErrorType) ->
            // When
            val errorType = exception.error()
            // Then
            assert(errorType == expectedErrorType)
        }
    }
}
