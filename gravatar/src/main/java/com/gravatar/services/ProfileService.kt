package com.gravatar.services

import com.gravatar.HttpResponseCode
import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.types.Hash
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar profiles.
 *
 * @param okHttpClient The OkHttp client to use for making network requests.
 * This client will be extended with Gravatar interceptors to set either API key or OAuth token.
 */
public class ProfileService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "ProfileService"
    }

    private val service = GravatarSdkDI.getGravatarV3Service(okHttpClient)

    /**
     * Fetches a Gravatar profile for the given hash or username.
     * This method throws any exception that occurs during the execution.
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile or null if profile not found
     */
    public suspend fun retrieve(hashOrUsername: String): Profile? = withContext(GravatarSdkDI.dispatcherIO) {
        val response = service.getProfileById(hashOrUsername)
        if (response.isSuccessful) {
            response.body() ?: error("Response body is null")
        } else {
            // Log the response body for debugging purposes if the response is not successful
            Logger.w(
                LOG_TAG,
                "Network call unsuccessful trying to get Gravatar profile: ${response.code()}",
            )
            if (response.code() == HttpResponseCode.HTTP_NOT_FOUND) {
                return@withContext null
            } else {
                throw HttpException(response)
            }
        }
    }

    /**
     * Fetches a Gravatar profile for the given hash or username.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile
     */
    public suspend fun retrieveCatching(hashOrUsername: String): GravatarResult<Profile, ErrorType> =
        runCatchingRequest {
            retrieve(hashOrUsername)
        }

    /**
     * Fetches a Gravatar profile for the given email address.
     * This method throws any exception that occurs during the execution.
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profile or null if profile not found
     */
    public suspend fun retrieve(email: Email): Profile? {
        return retrieve(email.hash())
    }

    /**
     * Fetches a Gravatar profile for the given email address.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveCatching(email: Email): GravatarResult<Profile, ErrorType> = runCatchingRequest {
        retrieve(email.hash())
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     * This method throws any exception that occurs during the execution.
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profile or null if profile not found
     */
    public suspend fun retrieve(hash: Hash): Profile? {
        return retrieve(hash.toString())
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveCatching(hash: Hash): GravatarResult<Profile, ErrorType> = runCatchingRequest {
        retrieve(hash)
    }

    /**
     * Fetches a Gravatar profile for the given username.
     * This method throws any exception that occurs during the execution.
     *
     * @param username The username to fetch the profile for
     * @return The fetched profile or null if profile not found
     */
    public suspend fun retrieveByUsername(username: String): Profile? {
        return retrieve(username)
    }

    /**
     * Fetches a Gravatar profile for the given username.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param username The username to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveByUsernameCatching(username: String): GravatarResult<Profile, ErrorType> =
        runCatchingRequest {
            retrieveByUsername(username)
        }

    /**
     * Checks if the given email address is associated with the already authorized Gravatar account.
     * This method throws any exception that occurs during the execution.
     *
     * @param oauthToken The OAuth token to use for authentication
     * @param email The email address to check
     * @return True if the email is associated with the account, false otherwise
     */
    public suspend fun checkAssociatedEmail(oauthToken: String, email: Email): Boolean =
        withContext(GravatarSdkDI.dispatcherIO) {
            val service = GravatarSdkDI.getGravatarV3Service(okHttpClient, oauthToken)

            val response = service.associatedEmail(email.hash().toString())
            if (response.isSuccessful) {
                response.body()?.associated ?: error("Response body is null")
            } else {
                // Log the response body for debugging purposes if the response is not successful
                Logger.w(
                    LOG_TAG,
                    "Network call unsuccessful trying to checkAssociatedEmail: ${response.code()}",
                )
                throw HttpException(response)
            }
        }

    /**
     * Checks if the given email address is associated with the already authorized Gravatar account.
     * This method will catch any exception that occurs during
     * the execution and return it as a [GravatarResult.Failure].
     *
     * @param oauthToken The OAuth token to use for authentication
     * @param email The email address to check
     * @return True if the email is associated with the account, false otherwise
     */
    public suspend fun checkAssociatedEmailCatching(
        oauthToken: String,
        email: Email,
    ): GravatarResult<Boolean, ErrorType> = runCatchingRequest {
        checkAssociatedEmail(oauthToken, email)
    }
}
