package com.gravatar.services

import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Identity
import com.gravatar.restapi.models.SelectAvatar
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar identities.
 */
public class IdentityService(private val okHttpClient: OkHttpClient? = null, oauthToken: String) {
    private companion object {
        const val LOG_TAG = "IdentityService"
    }

    /**
     * Retrieves a Gravatar identity by its hash.
     *
     * @param hash The hash of the identity to retrieve
     * @param oauthToken The OAuth token to use for authentication
     * @return The retrieved identity
     */
    public suspend fun retrieve(hash: String, oauthToken: String): Identity = withContext(GravatarSdkDI.dispatcherIO) {
        val service = GravatarSdkDI.getGravatarV3Service(okHttpClient, oauthToken)

        val response = service.getIdentity(hash)

        if (response.isSuccessful) {
            response.body() ?: error("Response body is null")
        } else {
            // Log the response body for debugging purposes if the response is not successful
            Logger.w(
                LOG_TAG,
                "Network call unsuccessful trying to retrieve Gravatar identities: ${response.code()}",
            )
            throw HttpException(response)
        }
    }

    /**
     * Retrieves a Gravatar identity by its hash.
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param hash The hash of the identity to retrieve
     * @param oauthToken The OAuth token to use for authentication
     * @return The result of the operation
     */
    public suspend fun retrieveCatching(hash: String, oauthToken: String): Result<Identity, ErrorType> =
        runCatchingRequest {
            retrieve(hash, oauthToken)
        }

    /**
     * Sets the avatar for the given email (hash).
     *
     * @param hash The hash of the email to set the avatar for
     * @param avatarId The ID of the avatar to set
     * @param oauthToken The OAuth token to use for authentication
     */
    public suspend fun setAvatar(hash: String, avatarId: String, oauthToken: String): Unit =
        withContext(GravatarSdkDI.dispatcherIO) {
            val service = GravatarSdkDI.getGravatarV3Service(okHttpClient, oauthToken)

            val response = service.setIdentityAvatar(hash, SelectAvatar { this.avatarId = avatarId })

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

    /**
     * Sets the avatar for the given email (hash).
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param hash The hash of the email to set the avatar for
     * @param avatarId The ID of the avatar to set
     * @param oauthToken The OAuth token to use for authentication
     * @return The result of the operation
     */
    public suspend fun setAvatarCatching(hash: String, avatarId: String, oauthToken: String): Result<Unit, ErrorType> =
        runCatchingRequest {
            setAvatar(hash, avatarId, oauthToken)
        }
}
