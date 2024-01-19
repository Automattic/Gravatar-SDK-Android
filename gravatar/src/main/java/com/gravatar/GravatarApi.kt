package com.gravatar

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class GravatarApi(okHttpClient: OkHttpClient? = null) {
    private val retrofit: Retrofit

    init {
        retrofit =
            Retrofit.Builder().apply {
                okHttpClient?.let { client(it) }
                baseUrl(GRAVATAR_API_BASE_URL)
            }.build()
    }

    private companion object {
        const val LOG_TAG = "Gravatar"
    }

    enum class ErrorType {
        SERVER,
        TIMEOUT,
        NETWORK,
        UNKNOWN,
    }

    fun uploadGravatar(
        file: File,
        email: String,
        accessToken: String,
        gravatarUploadListener: GravatarUploadListener,
    ) {
        val service = retrofit.create(GravatarApiService::class.java)
        val identity = MultipartBody.Part.createFormData("account", email)
        val filePart = MultipartBody.Part.createFormData("filedata", file.name, file.asRequestBody())

        service.uploadImage("Bearer $accessToken", identity, filePart).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    Handler(Looper.getMainLooper()).post {
                        if (response.isSuccessful) {
                            gravatarUploadListener.onSuccess()
                        } else {
                            // Log the response body for debugging purposes if the response is not successful
                            Log.w(LOG_TAG, "Network call unsuccessful trying to upload Gravatar: $response.body")
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

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable,
                ) {
                    val error: ErrorType =
                        when (t) {
                            is SocketTimeoutException -> ErrorType.TIMEOUT
                            is UnknownHostException -> ErrorType.NETWORK
                            else -> ErrorType.UNKNOWN
                        }
                    Handler(Looper.getMainLooper()).post {
                        gravatarUploadListener.onError(error)
                    }
                }
            },
        )
    }

    interface GravatarUploadListener {
        fun onSuccess()

        fun onError(errorType: ErrorType)
    }
}
