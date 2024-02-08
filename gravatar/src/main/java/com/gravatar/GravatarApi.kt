package com.gravatar

import com.gravatar.logger.Logger
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

class GravatarApi(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "Gravatar"
    }

    /**
     * Error types for Gravatar image upload
     */
    enum class ErrorType {
        /** server returned an error */
        SERVER,

        /** network request timed out */
        TIMEOUT,

        /** network is not available */
        NETWORK,

        /** An unknown error occurred */
        UNKNOWN,
    }

    val coroutineScope = CoroutineScope(GravatarSdkDI.dispatcherDefault)

    /**
     * Uploads a Gravatar image for the given email address.
     *
     * @param file The image file to upload
     * @param email The email address to associate the image with
     * @param accessToken The bearer token for the user's WordPress/Gravatar account
     * @param gravatarUploadListener The listener to notify of the upload result
     */
    fun uploadGravatar(
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
                            val error: ErrorType =
                                when (response.code()) {
                                    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.TIMEOUT
                                    in HttpResponseCode.SERVER_ERRORS -> ErrorType.SERVER
                                    else -> ErrorType.UNKNOWN
                                }
                            gravatarUploadListener.onError(error)
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

    /**
     * Listener for Gravatar image upload
     */
    interface GravatarUploadListener {
        /**
         * Called when the Gravatar image upload is successful
         */
        fun onSuccess()

        /**
         * Called when the Gravatar image upload fails
         *
         * @param errorType The type of error that occurred
         */
        fun onError(errorType: ErrorType)
    }
}
