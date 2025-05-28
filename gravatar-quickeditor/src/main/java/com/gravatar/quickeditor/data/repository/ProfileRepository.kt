package com.gravatar.quickeditor.data.repository

import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class ProfileRepository(
    private val tokenStorage: TokenStorage,
    private val profileService: ProfileService,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getProfile(email: Email): GravatarResult<Profile, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = profileService.retrieveAuthenticatedCatching(token)) {
                is GravatarResult.Success -> GravatarResult.Success(result.value)
                is GravatarResult.Failure -> GravatarResult.Failure(QuickEditorError.Request(result.error))
            }
        } ?: GravatarResult.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun updateProfile(
        email: Email,
        updateProfileRequest: UpdateProfileRequest,
    ): GravatarResult<Profile, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = profileService.updateProfileCatching(token, updateProfileRequest)) {
                is GravatarResult.Success -> GravatarResult.Success(result.value)
                is GravatarResult.Failure -> GravatarResult.Failure(QuickEditorError.Request(result.error))
            }
        } ?: GravatarResult.Failure(QuickEditorError.TokenNotFound)
    }
}
