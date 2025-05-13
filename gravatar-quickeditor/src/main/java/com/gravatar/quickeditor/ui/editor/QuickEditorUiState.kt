package com.gravatar.quickeditor.ui.editor

import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState

internal data class QuickEditorUiState(
    val email: Email,
    val page: QuickEditorPage,
    val pageNavigationEnabled: Boolean,
    val profile: ComponentState<Profile>? = null,
    val avatarCacheBuster: Long? = null,
) {
    val editAvatarButtonVisible: Boolean = pageNavigationEnabled && page == QuickEditorPage.AboutEditor
    val editAboutButtonVisible: Boolean = pageNavigationEnabled && page == QuickEditorPage.AvatarPicker
}
