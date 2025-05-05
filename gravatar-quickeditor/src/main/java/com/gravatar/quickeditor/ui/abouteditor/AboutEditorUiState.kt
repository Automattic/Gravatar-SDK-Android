package com.gravatar.quickeditor.ui.abouteditor

internal data class AboutEditorUiState(
    val aboutFields: AboutFields = AboutFields.EMPTY,
    val isLoading: Boolean = false,
)
