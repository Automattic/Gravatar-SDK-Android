package com.gravatar.publicapi.apis

import com.gravatar.publicapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.gravatar.publicapi.models.Error
import com.gravatar.publicapi.models.Profile

interface ProfileApi {
    /**
     * Profile for a specific hash
     * 
     * Responses:
     *  - 200: Expected response to a valid request
     *  - 0: unexpected error
     *
     * @param emailHash The emailHash of the profile to retrieve
     * @return [Call]<[Profile]>
     */
    @GET("{emailHash}.json")
    fun showProfileByUsername(@Path("emailHash") emailHash: kotlin.String): Call<Profile>

}
