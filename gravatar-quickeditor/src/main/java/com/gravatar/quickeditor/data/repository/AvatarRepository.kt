package com.gravatar.quickeditor.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.Result
import com.gravatar.types.Email
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class AvatarRepository(
    private val avatarService: AvatarService,
    private val tokenStorage: TokenStorage,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getAvatars(email: Email): Result<EmailAvatars, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val avatarsResult = avatarService.retrieveCatching(token, email.hash())) {
                is Result.Success -> {
                    val avatars = avatarsResult.value
                    Result.Success(
                        EmailAvatars(
                            avatars,
                            avatars.firstOrNull { it.selected == true }?.imageId,
                        ),
                    )
                }

                is Result.Failure -> {
                    Result.Failure(QuickEditorError.Request(avatarsResult.error))
                }
            }
        } ?: Result.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun selectAvatar(email: Email, avatarId: String): Result<Unit, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = avatarService.setAvatarCatching(email.hash().toString(), avatarId, token)) {
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
}

internal data class EmailAvatars(
    val avatars: List<Avatar>,
    val selectedAvatarId: String?,
)
