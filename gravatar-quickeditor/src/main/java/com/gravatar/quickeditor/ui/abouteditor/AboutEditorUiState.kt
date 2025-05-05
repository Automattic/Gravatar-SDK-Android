package com.gravatar.quickeditor.ui.abouteditor

internal data class AboutEditorUiState(
    val aboutFields: AboutFields = AboutFields.EMPTY,
    val isLoading: Boolean = false,
    val savingProfile: Boolean = false,
) {
    val formEnabled: Boolean = !savingProfile

    val saveEnabled: Boolean = !isLoading
}
