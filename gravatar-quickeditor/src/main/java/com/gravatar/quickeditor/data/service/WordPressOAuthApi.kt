package com.gravatar.quickeditor.data.service

import com.gravatar.quickeditor.data.models.WordPressOAuthToken
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface WordPressOAuthApi {
    @POST("/oauth2/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") user: String,
        @Field("grant_type") grantType: String,
    ): Response<WordPressOAuthToken>
}
