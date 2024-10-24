package com.gravatar.quickeditor.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class AvatarRepository(
    private val avatarService: AvatarService,
    private val tokenStorage: TokenStorage,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getAvatars(email: Email): GravatarResult<EmailAvatars, QuickEditorError> = withContext(dispatcher) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val avatarsResult = avatarService.retrieveCatching(token, email.hash())) {
                is GravatarResult.Success -> {
                    val avatars = avatarsResult.value
                    GravatarResult.Success(
                        EmailAvatars(
                            avatars,
                            avatars.firstOrNull { it.selected == true }?.imageId,
                        ),
                    )
                }

                is GravatarResult.Failure -> {
                    GravatarResult.Failure(QuickEditorError.Request(avatarsResult.error))
                }
            }
        } ?: GravatarResult.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun selectAvatar(email: Email, avatarId: String): GravatarResult<Unit, QuickEditorError> = withContext(
        dispatcher,
    ) {
        val token = tokenStorage.getToken(email.hash().toString())
        token?.let {
            when (val result = avatarService.setAvatarCatching(email.hash().toString(), avatarId, token)) {
                is GravatarResult.Success -> GravatarResult.Success(Unit)
                is GravatarResult.Failure -> GravatarResult.Failure(QuickEditorError.Request(result.error))
            }
        } ?: GravatarResult.Failure(QuickEditorError.TokenNotFound)
    }

    suspend fun uploadAvatar(
        email: Email,
        avatarUri: Uri,
        selectAvatar: Boolean,
    ): GravatarResult<Avatar, QuickEditorError> = withContext(dispatcher) {
        val hash = email.hash()
        val token = tokenStorage.getToken(hash.toString())
        token?.let {
            when (
                val result =
                    avatarService.uploadCatching(avatarUri.toFile(), hash, selectAvatar, token)
            ) {
                is GravatarResult.Success -> GravatarResult.Success(result.value)
                is GravatarResult.Failure -> GravatarResult.Failure(QuickEditorError.Request(result.error))
            }
        } ?: GravatarResult.Failure(QuickEditorError.TokenNotFound)
    }
}

internal data class EmailAvatars(
    val avatars: List<Avatar>,
    val selectedAvatarId: String?,
)
