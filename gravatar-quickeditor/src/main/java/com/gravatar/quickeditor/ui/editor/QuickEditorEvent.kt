package com.gravatar.quickeditor.ui.editor

internal sealed class QuickEditorEvent {
    data object Refresh : QuickEditorEvent()

    data object UpdateAvatarCache : QuickEditorEvent()
}
