package com.gravatar.quickeditor.ui.editor

internal sealed class QuickEditorAction {
    data object DismissEditor : QuickEditorAction()

    data object NotifyDismissIgnored : QuickEditorAction()
}
