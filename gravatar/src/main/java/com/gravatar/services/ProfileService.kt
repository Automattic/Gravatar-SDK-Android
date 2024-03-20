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
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar profiles.
 */
public class ProfileService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "ProfileService"
    }

    private val coroutineScope = CoroutineScope(GravatarSdkDI.dispatcherDefault)

    private fun fetch(hashOrUsername: String, getProfileListener: GravatarListener<UserProfiles>) {
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
                            getProfileListener.onError(httpErrorToErrorType(response.code()))
                        }
                    }
                }

                override fun onFailure(call: Call<UserProfiles>, t: Throwable) {
                    handleError(t, getProfileListener, coroutineScope)
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
    public fun fetch(email: Email, getProfileListener: GravatarListener<UserProfiles>) {
        fetch(email.hash(), getProfileListener = getProfileListener)
    }

    /**
     * Fetches a Gravatar profile for the given hash.
     *
     * @param hash The hash to fetch the profile for
     * @param getProfileListener The listener to notify of the fetch result
     */
    public fun fetch(hash: Hash, getProfileListener: GravatarListener<UserProfiles>) {
        fetch(hash.toString(), getProfileListener = getProfileListener)
    }

    /**
     * Fetches a Gravatar profile for the given username.
     *
     * @param username The username to fetch the profile for
     * @param getProfileListener The listener to notify of the fetch result
     */
    public fun fetchByUsername(username: String, getProfileListener: GravatarListener<UserProfiles>) {
        fetch(username, getProfileListener = getProfileListener)
    }
}
