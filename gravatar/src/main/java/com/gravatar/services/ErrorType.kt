package com.gravatar.services

import com.gravatar.HttpResponseCode
import com.gravatar.restapi.models.Error
import com.squareup.moshi.Moshi
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Objects

internal fun HttpException.errorTypeFromHttpCode(moshi: Moshi): ErrorType = when (code) {
    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.Timeout
    HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NotFound
    HttpResponseCode.HTTP_TOO_MANY_REQUESTS -> ErrorType.RateLimitExceeded
    HttpResponseCode.UNAUTHORIZED -> ErrorType.Unauthorized
    HttpResponseCode.INVALID_REQUEST -> {
        val error: Error? = runCatching {
            rawErrorBody?.let { moshi.adapter(Error::class.java).fromJson(it) }
        }.getOrNull()
        ErrorType.InvalidRequest(error)
    }
    HttpResponseCode.CONTENT_TOO_LARGE -> ErrorType.ContentLengthExceeded
    in HttpResponseCode.SERVER_ERRORS -> ErrorType.Server
    else -> ErrorType.Unknown("HTTP Code $code - ErrorBody $rawErrorBody")
}

internal fun Throwable.errorType(moshi: Moshi): ErrorType {
    return when (this) {
        is SocketTimeoutException -> ErrorType.Timeout
        is UnknownHostException -> ErrorType.Network
        is HttpException -> this.errorTypeFromHttpCode(moshi)
        else -> ErrorType.Unknown(message)
    }
}

/**
 * Error types for Gravatar API requests.
 */
public interface ErrorType {
    /** server returned an error */
    public data object Server : ErrorType

    /** network request timed out */
    public data object Timeout : ErrorType

    /** network is not available */
    public data object Network : ErrorType

    /** User or hash not found */
    public data object NotFound : ErrorType

    /** User or hash not found */
    public data object RateLimitExceeded : ErrorType

    /** User not authorized to perform given action **/
    public data object Unauthorized : ErrorType

    /** Content length exceeded **/
    public data object ContentLengthExceeded : ErrorType

    /**
     * An unknown error occurred
     *
     * @property errorMsg The error message, if available.
     */
    public class Unknown(public val errorMsg: String? = null) : ErrorType {
        override fun toString(): String = "Unknown(errorMsg=$errorMsg)"

        override fun equals(other: Any?): Boolean = other is Unknown && errorMsg == other.errorMsg

        override fun hashCode(): Int = Objects.hash(errorMsg)
    }

    /**
     * An error occurred while processing the request.
     *
     * @property error The detailed error that occurred, if returned from the server.
     */
    public class InvalidRequest(public val error: Error?) : ErrorType {
        override fun toString(): String = "InvalidRequest(error=$error)"

        override fun equals(other: Any?): Boolean = other is InvalidRequest && error == other.error

        override fun hashCode(): Int = Objects.hash(error)
    }
}
