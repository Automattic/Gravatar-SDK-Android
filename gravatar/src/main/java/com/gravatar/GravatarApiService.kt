package com.gravatar

import com.gravatar.api.apis.ProfilesApi
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.services.GravatarApi"),
    level = DeprecationLevel.WARNING,
)
internal interface GravatarApiService : ProfilesApi {
    @Multipart
    @POST("upload-image")
    suspend fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part identity: MultipartBody.Part,
        @Part data: MultipartBody.Part,
    ): Response<ResponseBody>
}
