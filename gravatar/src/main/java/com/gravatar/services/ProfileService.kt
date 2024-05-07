package com.gravatar.services

import com.gravatar.api.models.UserProfiles
import com.gravatar.logger.Logger
import com.gravatar.types.Email
import com.gravatar.types.Hash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar profiles.
 */
public class ProfileService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "ProfileService"
    }

    // Run onResponse and onError callbacks on the main thread
    private val coroutineScope = CoroutineScope(GravatarSdkDI.dispatcherMain)

    private fun fetchWithListener(
        hashOrUsername: String,
        getProfileListener: GravatarListener<UserProfiles, ErrorType>,
    ) {
        val service = GravatarSdkDI.getGravatarBaseService(okHttpClient)
        service.getProfile(hashOrUsername).enqueue(
            object : Callback<UserProfiles> {
                override fun onResponse(call: Call<UserProfiles>, response: Response<UserProfiles>) {
                    coroutineScope.launch {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                getProfileListener.onSuccess(data)
                            } else {
                                getProfileListener.onError(ErrorType.UNKNOWN)
                            }
                        } else {
                            // Log the response body for debugging purposes if the response is not successful
                            Logger.w(
                                LOG_TAG,
                                "Network call unsuccessful trying to get Gravatar profile: $response.body",
                            )
                            getProfileListener.onError(errorTypeFromHttpCode(response.code()))
                        }
                    }
                }

                override fun onFailure(call: Call<UserProfiles>, t: Throwable) {
                    coroutineScope.launch {
                        getProfileListener.onError(t.error())
                    }
                }
            },
        )
    }

    /**
     * Fetches a Gravatar profile for the given email address.
     *
     * @param email The email address to fetch the profile for
     * @param getProfileListener The listener to notify of the fetch result
     */
    public fun fetchWithListener(email: Email, getProfileListener: GravatarListener<UserProfiles, ErrorType>) {
        fetchWithListener(email.hash(), getProfileListener = getProfileListener)
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     *
     * @param hash The hash to fetch the profile for
     * @param getProfileListener The listener to notify of the fetch result
     */
    public fun fetchWithListener(hash: Hash, getProfileListener: GravatarListener<UserProfiles, ErrorType>) {
        fetchWithListener(hash.toString(), getProfileListener = getProfileListener)
    }

    /**
     * Fetches a Gravatar profile for the given username.
     *
     * @param username The username to fetch the profile for
     * @param getProfileListener The listener to notify of the fetch result
     */
    public fun fetchWithListenerByUsername(
        username: String,
        getProfileListener: GravatarListener<UserProfiles, ErrorType>,
    ) {
        fetchWithListener(username, getProfileListener = getProfileListener)
    }

    /**
     * Fetches a Gravatar profile for the given hash or username.
     *
     * @param hashOrUsername The hash or username to fetch the profile for
     * @return The fetched profile
     */
    public suspend fun fetch(hashOrUsername: String): Result<UserProfiles, ErrorType> {
        return suspendCoroutine {
            fetchWithListener(
                hashOrUsername,
                object : GravatarListener<UserProfiles, ErrorType> {
                    override fun onSuccess(response: UserProfiles) {
                        it.resume(Result.Success(response))
                    }

                    override fun onError(errorType: ErrorType) {
                        it.resume(Result.Failure(errorType))
                    }
                },
            )
        }
    }

    /**
     * Fetches a Gravatar profile for the given email address.
     *
     * @param email The email address to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetch(email: Email): Result<UserProfiles, ErrorType> {
        return fetch(email.hash())
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     *
     * @param hash The hash to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetch(hash: Hash): Result<UserProfiles, ErrorType> {
        return fetch(hash.toString())
    }

    /**
     * Fetches a Gravatar profile for the given username.
     *
     * @param username The username to fetch the profile for
     * @return The fetched profiles
     */
    public suspend fun fetchByUsername(username: String): Result<UserProfiles, ErrorType> {
        return fetch(username)
    }
}
