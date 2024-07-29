package com.gravatar.services

import com.gravatar.di.container.GravatarSdkContainer.Companion.instance
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

    private val service = GravatarSdkDI.getGravatarV1Service(okHttpClient)

    /**
     * Uploads a Gravatar image for the given email address.
     *
     * @param file The image file to upload
     * @param email The email address to associate the image with
     * @param wordpressBearerToken The bearer token for the user's WordPress/Gravatar account
     */
    @Deprecated("Use upload(file: File, oauthToken: String) instead", level = DeprecationLevel.WARNING)
    public suspend fun upload(file: File, email: Email, wordpressBearerToken: String): Result<Unit, ErrorType> {
        val identity = MultipartBody.Part.createFormData("account", email.toString())
        val filePart =
            MultipartBody.Part.createFormData("filedata", file.name, file.asRequestBody())

        return runCatchingService {
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
        }
    }

    /**
     * Uploads an image to be used as Gravatar avatar.
     *
     * @param file The image file to upload
     * @param oauthToken The OAuth token to use for authentication
     */
    public suspend fun upload(file: File, oauthToken: String): Unit = withContext(GravatarSdkDI.dispatcherIO) {
        val service = instance.getGravatarV3Service(okHttpClient, oauthToken)

        val filePart =
            MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())

        val response = service.uploadAvatar(filePart)

        if (response.isSuccessful) {
            Unit
        } else {
            // Log the response body for debugging purposes if the response is not successful
            Logger.w(
                LOG_TAG,
                "Network call unsuccessful trying to upload Gravatar: $response.body",
            )
            throw HttpException(response)
        }
    }

    /**
     * Uploads an image to be used as Gravatar avatar.
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param file The image file to upload
     * @param oauthToken The OAuth token to use for authentication
     * @return The result of the operation
     */
    public suspend fun uploadCatching(file: File, oauthToken: String): Result<Unit, ErrorType> = runCatchingRequest {
        upload(file, oauthToken)
    }
}
