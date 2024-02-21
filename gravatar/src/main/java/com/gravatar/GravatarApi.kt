package com.gravatar

import com.gravatar.logger.Logger
import com.gravatar.models.UserProfiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

public class GravatarApi(private var okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "Gravatar"
    }

    /**
     * Error types for Gravatar image upload
     */
    public enum class ErrorType {
        /** server returned an error */
        SERVER,

        /** network request timed out */
        TIMEOUT,

        /** network is not available */
        NETWORK,

        /** User or hash not found */
        NOT_FOUND,

        /** An unknown error occurred */
        UNKNOWN,
    }

    private val coroutineScope = CoroutineScope(GravatarSdkDI.dispatcherDefault)

    private fun httpErrorToErrorType(code: Int): ErrorType = when (code) {
        HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.TIMEOUT
        HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NOT_FOUND
        in HttpResponseCode.SERVER_ERRORS -> ErrorType.SERVER
        else -> ErrorType.UNKNOWN
    }

    /**
     * Uploads a Gravatar image for the given email address.
     *
     * @param file The image file to upload
     * @param email The email address to associate the image with
     * @param accessToken The bearer token for the user's WordPress/Gravatar account
     * @param gravatarUploadListener The listener to notify of the upload result
     */
    public fun uploadGravatar(
        file: File,
        email: String,
        accessToken: String,
        gravatarUploadListener: GravatarUploadListener,
    ) {
        val service = GravatarSdkDI.getGravatarApiService(okHttpClient)
        val identity = MultipartBody.Part.createFormData("account", email)
        val filePart =
            MultipartBody.Part.createFormData("filedata", file.name, file.asRequestBody())

        service.uploadImage("Bearer $accessToken", identity, filePart).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    coroutineScope.launch {
                        if (response.isSuccessful) {
                            gravatarUploadListener.onSuccess()
                        } else {
                            // Log the response body for debugging purposes if the response is not successful
                            Logger.w(
                                LOG_TAG,
                                "Network call unsuccessful trying to upload Gravatar: $response.body",
                            )
                            gravatarUploadListener.onError(httpErrorToErrorType(response.code()))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val error: ErrorType =
                        when (t) {
                            is SocketTimeoutException -> ErrorType.TIMEOUT
                            is UnknownHostException -> ErrorType.NETWORK
                            else -> ErrorType.UNKNOWN
                        }
                    coroutineScope.launch {
                        gravatarUploadListener.onError(error)
                    }
                }
            },
        )
    }

    public fun getProfile(hash: String, getProfileListener: GetProfileListener) {
        val service = GravatarSdkDI.getGravatarBaseService(okHttpClient)
        service.getProfile(hash).enqueue(
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
                    val error: ErrorType =
                        when (t) {
                            is SocketTimeoutException -> ErrorType.TIMEOUT
                            is UnknownHostException -> ErrorType.NETWORK
                            else -> ErrorType.UNKNOWN
                        }
                    coroutineScope.launch {
                        getProfileListener.onError(error)
                    }
                }
            },
        )
    }

    /**
     * Listener for Gravatar image upload
     */
    public interface GravatarUploadListener {
        /**
         * Called when the Gravatar image upload is successful
         */
        public fun onSuccess()

        /**
         * Called when the Gravatar image upload fails
         *
         * @param errorType The type of error that occurred
         */
        public fun onError(errorType: ErrorType)
    }

    /**
     * Listener for Gravatar image upload
     */
    public interface GetProfileListener {
        /**
         * Called when the Gravatar image upload is successful
         */
        public fun onSuccess(userProfiles: UserProfiles)

        /**
         * Called when the Gravatar image upload fails
         *
         * @param errorType The type of error that occurred
         */
        public fun onError(errorType: ErrorType)
    }
}
