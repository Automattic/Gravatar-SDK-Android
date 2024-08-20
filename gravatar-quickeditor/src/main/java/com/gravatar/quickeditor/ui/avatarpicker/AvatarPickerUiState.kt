package com.gravatar.quickeditor.ui.avatarpicker

import com.gravatar.quickeditor.data.repository.IdentityAvatars
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState

internal data class AvatarPickerUiState(
    val email: Email,
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val profile: ComponentState<Profile>? = null,
    val identityAvatars: IdentityAvatars? = null,
    val selectingAvatarId: String? = null,
) {
    val avatars: List<AvatarUi>? = identityAvatars?.mapToUiModel()

    private fun IdentityAvatars.mapToUiModel(): List<AvatarUi.Uploaded> {
        return this.avatars.map { avatar ->
            AvatarUi.Uploaded(
                avatar = avatar,
                isSelected = avatar.imageId == (selectingAvatarId ?: selectedAvatarId),
                isLoading = avatar.imageId == selectingAvatarId,
            )
        }
    }
}

internal sealed class AvatarUi(val avatarId: String) {
    data class Uploaded(
        val avatar: Avatar,
        val isSelected: Boolean,
        val isLoading: Boolean,
    ) : AvatarUi(avatar.imageId)
}
