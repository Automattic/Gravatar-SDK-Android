package com.gravatar.quickeditor.data.models

import com.gravatar.services.ErrorType

internal sealed class QuickEditorError {
    data object TokenNotFound : QuickEditorError()

    data object Unknown : QuickEditorError()

    data class Request(
        val type: ErrorType,
    ) : QuickEditorError()
}
