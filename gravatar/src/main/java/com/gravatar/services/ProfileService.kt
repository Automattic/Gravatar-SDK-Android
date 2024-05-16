package com.gravatar.services

import com.gravatar.api.models.Profile
import com.gravatar.logger.Logger
import com.gravatar.types.Email
import com.gravatar.types.Hash
import okhttp3.OkHttpClient
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar profiles.
 */
public class ProfileService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "ProfileService"
    }

    /**
     * Fetches a Gravatar profile for the given hash or username.
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile
     */
    public suspend fun fetch(hashOrUsername: String): Result<Profile, ErrorType> {
        val service = GravatarSdkDI.getGravatarApiV3Service(okHttpClient)
        @Suppress("TooGenericExceptionCaught")
        return try {
            val response = service.getProfileById(hashOrUsername)
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
        } catch (ex: Exception) {
            Result.Failure(ex.error())
        }
    }

    /**
     * Fetches a Gravatar profile for the given email address.
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetch(email: Email): Result<Profile, ErrorType> {
        return fetch(email.hash())
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetch(hash: Hash): Result<Profile, ErrorType> {
        return fetch(hash.toString())
    }

    /**
     * Fetches a Gravatar profile for the given username.
     *
     * @param username The username to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetchByUsername(username: String): Result<Profile, ErrorType> {
        return fetch(username)
    }
}
