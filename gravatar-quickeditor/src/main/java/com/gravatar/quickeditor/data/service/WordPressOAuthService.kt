package com.gravatar.quickeditor.data.service

import com.google.gson.GsonBuilder
import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.cancellation.CancellationException

private val retrofit = Retrofit.Builder()
    .baseUrl("https://public-api.wordpress.com")
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .build()

internal class WordPressOAuthService(
    private val service: WordPressOAuthApi = retrofit.create(WordPressOAuthApi::class.java),
) {
    suspend fun getAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
    ): Result<String, ErrorType> = withContext(Dispatchers.IO) {
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        try {
            val response = service.getToken(
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
                Result.Failure(ErrorType.UNKNOWN)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (ex: Exception) {
            Result.Failure(ErrorType.SERVER)
        }
    }
}
