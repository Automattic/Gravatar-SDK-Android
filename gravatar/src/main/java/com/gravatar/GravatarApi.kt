package com.gravatar

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Companion.FORM
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class GravatarApi {
    private companion object {
        const val API_BASE_URL = "https://api.gravatar.com/v1/"
        const val DEFAULT_TIMEOUT = 15000
        const val LOG_TAG = "Gravatar"
    }

    enum class ErrorType {
        SERVER,
        TIMEOUT,
        NETWORK,
        UNKNOWN
    }

    private fun createClient(accessToken: String): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        // This should help with recovery from the SocketTimeoutException
        // https://github.com/square/okhttp/issues/3146#issuecomment-311158567
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .connectionPool(
                ConnectionPool(0, 1, TimeUnit.NANOSECONDS)
            )


        // add oAuth token usage
        httpClientBuilder.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .method(original.method, original.body)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        })
        return httpClientBuilder.build()
    }

    private fun prepareGravatarUpload(email: String, file: File): Request {
        return Request.Builder()
            .url(API_BASE_URL + "upload-image")
            .post(
                MultipartBody.Builder()
                    .setType(FORM)
                    .addFormDataPart("account", email)
                    .addFormDataPart("filedata", file.name, StreamingRequest(file))
                    .build()
            )
            .build()
    }

    fun uploadGravatar(
        file: File, email: String, accessToken: String,
        gravatarUploadListener: GravatarUploadListener
    ) {
        val request = prepareGravatarUpload(email, file)
        createClient(accessToken).newCall(request).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Handler(Looper.getMainLooper()).post {
                        if (response.isSuccessful) {
                            gravatarUploadListener.onSuccess()
                        } else {
                            // Log the response body for debugging purposes if the response is not successful
                            Log.w(
                                LOG_TAG,
                                "Network call unsuccessful trying to upload Gravatar: $response.body"
                            )
                            val error: ErrorType = when (response.code) {
                                408 -> ErrorType.TIMEOUT
                                in 500..599 -> ErrorType.SERVER
                                else -> ErrorType.UNKNOWN
                            }
                            gravatarUploadListener.onError(error)
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    val error: ErrorType = when (e) {
                        is SocketTimeoutException -> ErrorType.TIMEOUT
                        is UnknownHostException -> ErrorType.NETWORK
                        else -> ErrorType.UNKNOWN
                    }
                    Handler(Looper.getMainLooper()).post {
                        gravatarUploadListener.onError(error)
                    }
                }
            })
    }

    interface GravatarUploadListener {
        fun onSuccess()
        fun onError(errorType: ErrorType)
    }
}
