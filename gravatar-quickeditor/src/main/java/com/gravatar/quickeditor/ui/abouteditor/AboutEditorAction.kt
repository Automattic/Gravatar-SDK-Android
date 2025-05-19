package com.gravatar.quickeditor.ui.abouteditor

import com.gravatar.restapi.models.Profile

internal sealed class AboutEditorAction {
    data class ProfileUpdated(val profile: Profile) : AboutEditorAction()

    data object ProfileUpdateFailed : AboutEditorAction()

    data object CloseEditor : AboutEditorAction()

    data object NotifyDismissIgnored : AboutEditorAction()

    data object InvokeAuthFailed : AboutEditorAction()
}
