package com.gravatar.services

import com.gravatar.HttpResponseCode
import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.types.Hash
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.gravatar.api.models.Profile as LegacyProfile
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar profiles.
 */
public class ProfileService(okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "ProfileService"
    }

    private val deprecatedService = GravatarSdkDI.getGravatarApiV3Service(okHttpClient)
    private val service = GravatarSdkDI.getGravatarV3Service(okHttpClient)

    /**
     * Fetches a Gravatar profile for the given hash or username.
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile
     */
    @Deprecated(
        "This class is deprecated and will be removed in a future release.",
        replaceWith = ReplaceWith("com.gravatar.services.ProfileService.retrieve"),
        level = DeprecationLevel.WARNING,
    )
    public suspend fun fetch(hashOrUsername: String): Result<LegacyProfile, ErrorType> = runCatchingService {
        withContext(GravatarSdkDI.dispatcherIO) {
            val response = deprecatedService.getProfileById(hashOrUsername)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Result.Success(data)
                } else {
                    Result.Failure(ErrorType.UNKNOWN)
                }
            } else {
                // Log the response body for debugging purposes if the response is not successful
                Logger.w(
                    LOG_TAG,
                    "Network call unsuccessful trying to get Gravatar profile: $response.body",
                )
                Result.Failure(errorTypeFromHttpCode(response.code()))
            }
        }
    }

    /**
     * Fetches a Gravatar profile for the given email address.
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profiles
     */
    @Deprecated(
        "This class is deprecated and will be removed in a future release.",
        replaceWith = ReplaceWith("com.gravatar.services.ProfileService.retrieve"),
        level = DeprecationLevel.WARNING,
    )
    public suspend fun fetch(email: Email): Result<LegacyProfile, ErrorType> {
        return fetch(email.hash())
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profiles
     */
    @Deprecated(
        "This class is deprecated and will be removed in a future release.",
        replaceWith = ReplaceWith("com.gravatar.services.ProfileService.retrieve"),
        level = DeprecationLevel.WARNING,
    )
    public suspend fun fetch(hash: Hash): Result<LegacyProfile, ErrorType> {
        return fetch(hash.toString())
    }

    /**
     * Fetches a Gravatar profile for the given username.
     *
     * @param username The username to fetch the profile for
     * @return The fetched profiles
     */
    @Deprecated(
        "This class is deprecated and will be removed in a future release.",
        replaceWith = ReplaceWith("com.gravatar.services.ProfileService.retrieveByUsername"),
        level = DeprecationLevel.WARNING,
    )
    public suspend fun fetchByUsername(username: String): Result<LegacyProfile, ErrorType> {
        return fetch(username)
    }

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
