package com.gravatar.quickeditor.data.service

import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal class WordPressOAuthService(
    private val wordPressOAuthApi: WordPressOAuthApi,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
    ): Result<String, ErrorType> = withContext(dispatcher) {
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        try {
            val response = wordPressOAuthApi.getToken(
                clientId,
                redirectUri,
                clientSecret,
                code,
                "authorization_code",
            )
            val body = response.body()
            if (body != null) {
                Result.Success(body.token)
            } else {
                Result.Failure(ErrorType.Unknown)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (ex: Exception) {
            Result.Failure(ErrorType.Server)
        }
    }
}
