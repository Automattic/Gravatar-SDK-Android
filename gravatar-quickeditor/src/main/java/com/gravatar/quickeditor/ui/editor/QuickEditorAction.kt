package com.gravatar.quickeditor.ui.editor

internal sealed class QuickEditorAction {
    data object ConfirmEditorDismissal : QuickEditorAction()

    data object DismissEditor : QuickEditorAction()
}
