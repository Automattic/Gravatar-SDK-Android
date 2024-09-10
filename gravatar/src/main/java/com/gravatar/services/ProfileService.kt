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
 */
public class ProfileService(okHttpClient: OkHttpClient? = null) {
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
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile
     */
    public suspend fun retrieveCatching(hashOrUsername: String): Result<Profile, ErrorType> = runCatchingRequest {
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
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveCatching(email: Email): Result<Profile, ErrorType> = runCatchingRequest {
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
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveCatching(hash: Hash): Result<Profile, ErrorType> = runCatchingRequest {
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
     * This method will catch any exception that occurs during the execution and return it as a [Result.Failure].
     *
     * @param username The username to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun retrieveByUsernameCatching(username: String): Result<Profile, ErrorType> = runCatchingRequest {
        retrieveByUsername(username)
    }
}
