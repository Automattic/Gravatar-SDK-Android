package com.gravatar.services

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
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

/**
 * Service for managing Gravatar avatars.
 */
public class AvatarService(private val okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "AvatarService"
    }

    private val coroutineScope = CoroutineScope(GravatarSdkDI.dispatcherDefault)

    /**
     * Uploads a Gravatar image for the given email address.
     *
     * @param file The image file to upload
     * @param email The email address to associate the image with
     * @param accessToken The bearer token for the user's WordPress/Gravatar account
     * @param gravatarUploadListener The listener to notify of the upload result
     */
    public fun upload(file: File, email: String, accessToken: String, gravatarUploadListener: GravatarListener<Unit>) {
        val service = GravatarSdkDI.getGravatarApiService(okHttpClient)
        val identity = MultipartBody.Part.createFormData("account", email.toString())
        val filePart =
            MultipartBody.Part.createFormData("filedata", file.name, file.asRequestBody())

        service.uploadImage("Bearer $accessToken", identity, filePart).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    coroutineScope.launch {
                        if (response.isSuccessful) {
                            gravatarUploadListener.onSuccess(Unit)
                        } else {
                            // Log the response body for debugging purposes if the response is not successful
                            Logger.w(
                                LOG_TAG,
                                "Network call unsuccessful trying to upload Gravatar: $response.body",
                            )
                            gravatarUploadListener.onError(errorTypeFromHttpCode(response.code()))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    handleError(t, gravatarUploadListener, coroutineScope)
                }
            },
        )
    }
}
