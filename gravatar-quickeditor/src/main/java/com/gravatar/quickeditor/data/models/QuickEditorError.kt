package com.gravatar.quickeditor.data.models

internal sealed class QuickEditorError {
    data object TokenNotFound : QuickEditorError()

    data object Unknown : QuickEditorError()
}
