package com.gravatar.quickeditor.ui.editor

import com.gravatar.restapi.models.Profile

internal sealed class QuickEditorEvent {
    data object Refresh : QuickEditorEvent()

    data object UpdateAvatarCache : QuickEditorEvent()

    class OnProfileUpdated(val profile: Profile) : QuickEditorEvent()

    data object OnEditAvatarClicked : QuickEditorEvent()

    data object OnEditAboutClicked : QuickEditorEvent()

    data object OnConfirmDismissal : QuickEditorEvent()
}
