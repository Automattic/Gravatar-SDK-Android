package com.gravatar.services

import com.gravatar.logger.Logger
import com.gravatar.types.Email
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar avatars.
 */
public class AvatarService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "AvatarService"
    }

    /**
     * Uploads a Gravatar image for the given email address.
     *
     * @param file The image file to upload
     * @param email The email address to associate the image with
     * @param wordpressBearerToken The bearer token for the user's WordPress/Gravatar account
     */
    public suspend fun upload(file: File, email: Email, wordpressBearerToken: String): Result<Unit, ErrorType> {
        val service = GravatarSdkDI.getGravatarApiV1Service(okHttpClient)
        val identity = MultipartBody.Part.createFormData("account", email.toString())
        val filePart =
            MultipartBody.Part.createFormData("filedata", file.name, file.asRequestBody())

        @Suppress("TooGenericExceptionCaught")
        return try {
            withContext(GravatarSdkDI.dispatcherIO) {
                val response = service.uploadImage("Bearer $wordpressBearerToken", identity, filePart)

                if (response.isSuccessful) {
                    Result.Success(Unit)
                } else {
                    // Log the response body for debugging purposes if the response is not successful
                    Logger.w(
                        LOG_TAG,
                        "Network call unsuccessful trying to upload Gravatar: $response.body",
                    )
                    Result.Failure(errorTypeFromHttpCode(response.code()))
                }
            }
        } catch (ex: Exception) {
            Result.Failure(ex.error())
        }
    }
}
