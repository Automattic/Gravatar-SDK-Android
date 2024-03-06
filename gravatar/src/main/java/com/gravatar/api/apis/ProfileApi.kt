package com.gravatar.api.apis

import com.gravatar.api.infrastructure.CollectionFormats.*
import com.gravatar.api.models.UserProfiles
import retrofit2.Call
import retrofit2.http.*

interface ProfileApi {
    /**
     * Profile for a specific hash
     *
     * Responses:
     *  - 200: Expected response to a valid request
     *  - 0: unexpected error
     *
     * @param hash The email&#39;s hash of the profile to retrieve.
     * @return [Call]<[UserProfiles]>
     */
    @GET("{hash}.json")
    fun getProfile(
        @Path("hash") hash: kotlin.String,
    ): Call<UserProfiles>
}
