package com.gravatar.quickeditor.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Identity
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
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
            val avatarsResult = getAvatarsAsync(token).await()
            val identityResult = getIdentityAsync(email, token).await()
            if (avatarsResult is Result.Success && identityResult is Result.Success) {
                Result.Success(
                    IdentityAvatars(
                        avatarsResult.value,
                        identityResult.value.imageId,
                    ),
                )
            } else {
                val quickEditorError: QuickEditorError = (avatarsResult to identityResult).quickEditorError
                Result.Failure(quickEditorError)
            }
        } ?: Result.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun selectAvatar(email: Email, avatarId: String): Result<Unit, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = identityService.setAvatarCatching(email.hash().toString(), avatarId, token)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> Result.Failure(QuickEditorError.Request(result.error))
            }
        } ?: Result.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun uploadAvatar(email: Email, avatarUri: Uri): Result<Unit, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = avatarService.uploadCatching(avatarUri.toFile(), token)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> Result.Failure(QuickEditorError.Request(result.error))
            }
        } ?: Result.Failure(QuickEditorError.TokenNotFound)
    }

    private suspend fun getAvatarsAsync(token: String): Deferred<Result<List<Avatar>, ErrorType>> = coroutineScope {
        async { avatarService.retrieveCatching(token) }
    }

    private suspend fun getIdentityAsync(email: Email, token: String): Deferred<Result<Identity, ErrorType>> =
        coroutineScope {
            async { identityService.retrieveCatching(hash = email.hash().toString(), oauthToken = token) }
        }
}

/**
 * Take first or second if available, otherwise [QuickEditorError.Unknown]
 */
private val Pair<Result<List<Avatar>, ErrorType>, Result<Identity, ErrorType>>.quickEditorError: QuickEditorError
    get() = ((this.first as? Result.Failure)?.error ?: (this.second as? Result.Failure)?.error)
        ?.let { QuickEditorError.Request(it) }
        ?: QuickEditorError.Unknown

internal data class IdentityAvatars(
    val avatars: List<Avatar>,
    val selectedAvatarId: String?,
)
