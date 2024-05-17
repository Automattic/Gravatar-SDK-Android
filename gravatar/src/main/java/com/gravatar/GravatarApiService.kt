package com.gravatar

import com.gravatar.api.apis.ProfilesApi
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface GravatarApiService : ProfilesApi {
    @Multipart
    @POST("upload-image")
    fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part identity: MultipartBody.Part,
        @Part data: MultipartBody.Part,
    ): Call<ResponseBody>
}
