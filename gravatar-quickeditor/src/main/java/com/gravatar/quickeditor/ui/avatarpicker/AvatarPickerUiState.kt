package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import com.gravatar.quickeditor.data.repository.EmailAvatars
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.services.ErrorType
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState

internal data class AvatarPickerUiState(
    val email: Email,
    val avatarPickerContentLayout: AvatarPickerContentLayout,
    val isLoading: Boolean = false,
    val error: SectionError? = null,
    val profile: ComponentState<Profile>? = null,
    val emailAvatars: EmailAvatars? = null,
    val selectingAvatarId: String? = null,
    val uploadingAvatar: Uri? = null,
    val scrollToIndex: Int? = null,
    val failedUploads: Set<AvatarUploadFailure> = emptySet(),
    val failedUploadDialog: AvatarUploadFailure? = null,
    val avatarUpdates: Int = 0,
) {
    val avatarsSectionUiState: AvatarsSectionUiState? = emailAvatars?.mapToUiModel()?.let {
        AvatarsSectionUiState(
            avatars = it,
            scrollToIndex = scrollToIndex,
            uploadButtonEnabled = uploadingAvatar == null,
            avatarPickerContentLayout = avatarPickerContentLayout,
        )
    }

    private fun EmailAvatars.mapToUiModel(): List<AvatarUi> {
        return mutableListOf<AvatarUi>().apply {
            uploadingAvatar?.let {
                add(
                    AvatarUi.Local(
                        uri = uploadingAvatar,
                        isLoading = true,
                    ),
                )
            }
            addAll(
                this@AvatarPickerUiState.failedUploads.reversed().map { localAvatar ->
                    AvatarUi.Local(
                        uri = localAvatar.uri,
                        isLoading = false,
                    )
                },
            )
            addAll(
                this@mapToUiModel.avatars.map { avatar ->
                    AvatarUi.Uploaded(
                        avatar = avatar,
                        isSelected = avatar.imageId == (selectingAvatarId ?: selectedAvatarId),
                        isLoading = avatar.imageId == selectingAvatarId,
                    )
                },
            )
        }.toList()
    }
}

internal sealed class SectionError {
    data object ServerError : SectionError()

    data class InvalidToken(val showLogin: Boolean) : SectionError()

    data object Unknown : SectionError()

    data object NoInternetConnection : SectionError()
}

internal data class AvatarsSectionUiState(
    val avatarPickerContentLayout: AvatarPickerContentLayout,
    val avatars: List<AvatarUi>,
    val scrollToIndex: Int?,
    val uploadButtonEnabled: Boolean,
)

internal data class AvatarUploadFailure(
    val uri: Uri,
    val error: ErrorType?,
)

internal sealed class AvatarUi(val avatarId: String) {
    data class Uploaded(
        val avatar: Avatar,
        val isSelected: Boolean,
        val isLoading: Boolean,
    ) : AvatarUi(avatar.imageId)

    data class Local(
        val uri: Uri,
        val isLoading: Boolean,
    ) : AvatarUi(uri.toString())
}
