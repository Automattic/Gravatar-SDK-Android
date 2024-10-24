package com.gravatar.services

import com.gravatar.di.container.GravatarSdkContainer.Companion.instance
import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.SetEmailAvatarRequest
import com.gravatar.types.Hash
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar avatars.
 *
 * @param okHttpClient The OkHttp client to use for making network requests.
 * This client will be extended with Gravatar interceptors to set either API key or OAuth token.
 */
public class AvatarService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "AvatarService"
    }

    /**
     * Uploads an image to be used as Gravatar avatar.
     *
     * @param file The image file to upload
     * @param hash The hash of the email to associate the avatar with
     * @param selectAvatar Determines if the uploaded image should be set automatically as the avatar for the given hash
     * @param oauthToken The OAuth token to use for authentication
     */
    public suspend fun upload(file: File, hash: Hash, selectAvatar: Boolean, oauthToken: String): Avatar =
        runThrowingExceptionRequest {
            withContext(GravatarSdkDI.dispatcherIO) {
                val service = instance.getGravatarV3Service(okHttpClient, oauthToken)

                val filePart =
                    MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())

                val response = service.uploadAvatar(
                    data = filePart,
                    selectedEmailHash = hash.toString(),
                    selectAvatar = selectAvatar,
                )

                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    // Log the response body for debugging purposes if the response is not successful
                    Logger.w(
                        LOG_TAG,
                        "Network call unsuccessful trying to upload Gravatar: $response.body",
                    )
                    throw HttpException(response)
                }
            }
        }

    /**
     * Uploads an image to be used as Gravatar avatar.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param file The image file to upload
     * @param hash The hash of the email to associate the avatar with
     * @param selectAvatar Determines if the uploaded image should be set automatically as the avatar for the given hash
     * @param oauthToken The OAuth token to use for authentication
     * @return The result of the operation
     */
    public suspend fun uploadCatching(
        file: File,
        hash: Hash,
        selectAvatar: Boolean,
        oauthToken: String,
    ): GravatarResult<Avatar, ErrorType> = runCatchingRequest {
        upload(file, hash, selectAvatar, oauthToken)
    }

    /**
     * Retrieves a list of available avatars for the authenticated user.
     *
     * @param oauthToken The OAuth token to use for authentication
     * @param hash The hash of the email to associate the avatars with
     * @return The list of avatars
     */
    public suspend fun retrieve(oauthToken: String, hash: Hash): List<Avatar> = runThrowingExceptionRequest {
        withContext(GravatarSdkDI.dispatcherIO) {
            val service = instance.getGravatarV3Service(okHttpClient, oauthToken)

            val response = service.getAvatars(selectedEmailHash = hash.toString())

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
    }

    /**
     * Retrieves a list of available avatars for the authenticated user.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param oauthToken The OAuth token to use for authentication
     * @param hash The hash of the email to associate the avatars with
     * @return The list of avatars
     */
    public suspend fun retrieveCatching(oauthToken: String, hash: Hash): GravatarResult<List<Avatar>, ErrorType> =
        runCatchingRequest {
            retrieve(oauthToken, hash)
        }

    /**
     * Sets the avatar for the given email (hash).
     *
     * @param hash The hash of the email to set the avatar for
     * @param avatarId The ID of the avatar to set
     * @param oauthToken The OAuth token to use for authentication
     */
    public suspend fun setAvatar(hash: String, avatarId: String, oauthToken: String): Unit =
        runThrowingExceptionRequest {
            withContext(GravatarSdkDI.dispatcherIO) {
                val service = GravatarSdkDI.getGravatarV3Service(okHttpClient, oauthToken)

                val response = service.setEmailAvatar(avatarId, SetEmailAvatarRequest { emailHash = hash })

                if (response.isSuccessful) {
                    Unit
                } else {
                    // Log the response body for debugging purposes if the response is not successful
                    Logger.w(
                        LOG_TAG,
                        "Network call unsuccessful trying to set Gravatar avatar: $response.body",
                    )
                    throw HttpException(response)
                }
            }
        }

    /**
     * Sets the avatar for the given email (hash).
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param hash The hash of the email to set the avatar for
     * @param avatarId The ID of the avatar to set
     * @param oauthToken The OAuth token to use for authentication
     * @return The result of the operation
     */
    public suspend fun setAvatarCatching(
        hash: String,
        avatarId: String,
        oauthToken: String,
    ): GravatarResult<Unit, ErrorType> = runCatchingRequest {
        setAvatar(hash, avatarId, oauthToken)
    }
}
