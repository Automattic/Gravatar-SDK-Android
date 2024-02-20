package com.gravatar

import com.gravatar.models.UserProfiles
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

internal interface GravatarApiService {
    @Multipart
    @POST("upload-image")
    fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part identity: MultipartBody.Part,
        @Part data: MultipartBody.Part,
    ): Call<ResponseBody>

    @GET("{hash}.json")
    fun getProfile(
        @Path("hash") hash: String,
    ): Call<UserProfiles>
}
