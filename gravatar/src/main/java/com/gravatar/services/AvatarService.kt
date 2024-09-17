package com.gravatar.services

import com.gravatar.di.container.GravatarSdkContainer.Companion.instance
import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Avatar
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

    /**
     * Retrieves a list of available avatars for the authenticated user.
     *
     * @param oauthToken The OAuth token to use for authentication
     * @return The list of avatars
     */
    public suspend fun retrieve(oauthToken: String): List<Avatar> = withContext(GravatarSdkDI.dispatcherIO) {
        val service = instance.getGravatarV3Service(okHttpClient, oauthToken)

        val response = service.getAvatars()

        if (response.isSuccessful) {
            response.body() ?: error("Response body is null")
        } else {
            // Log the response body for debugging purposes if the response is not successful
            Logger.w(
                LOG_TAG,
                "Network call unsuccessful trying to get Gravatar avatars: ${response.code()}",
            )
            throw HttpException(response)
        }
    }

    /**
     * Retrieves a list of available avatars for the authenticated user.
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param oauthToken The OAuth token to use for authentication
     * @return The list of avatars
     */
    public suspend fun retrieveCatching(oauthToken: String): Result<List<Avatar>, ErrorType> = runCatchingRequest {
        retrieve(oauthToken)
    }
}
