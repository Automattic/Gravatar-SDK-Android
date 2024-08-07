package com.gravatar.quickeditor.data.repository

import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Identity
import com.gravatar.services.AvatarService
import com.gravatar.services.IdentityService
import com.gravatar.services.Result
import com.gravatar.types.Email
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

internal class AvatarRepository(
    private val identityService: IdentityService,
    private val avatarService: AvatarService,
    private val tokenStorage: TokenStorage,
    private val dispatcher: CoroutineDispatcher,
) {
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getAvatars(email: Email): Result<IdentityAvatars, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            try {
                val avatars = getAvatarsAsync(token).await()
                val identity = getIdentityAsync(email, token).await()
                Result.Success(IdentityAvatars(avatars, identity.imageId))
            } catch (e: Exception) {
                Result.Failure(QuickEditorError.Unknown)
            }
        } ?: Result.Failure(QuickEditorError.TokenNotFound)
    }

    private suspend fun getAvatarsAsync(token: String): Deferred<List<Avatar>> = coroutineScope {
        async { avatarService.retrieve(token) }
    }

    private suspend fun getIdentityAsync(email: Email, token: String): Deferred<Identity> = coroutineScope {
        async { identityService.retrieve(hash = email.hash().toString(), oauthToken = token) }
    }
}

internal data class IdentityAvatars(
    val avatars: List<Avatar>,
    val selectedAvatarId: String,
)
