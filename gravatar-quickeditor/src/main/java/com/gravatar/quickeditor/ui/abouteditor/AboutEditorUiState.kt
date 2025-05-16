package com.gravatar.quickeditor.ui.abouteditor

internal data class AboutEditorUiState(
    val aboutFields: Set<AboutEditorField> = emptySet(),
    val isLoading: Boolean = false,
    val savingProfile: Boolean = false,
    val discardChangesDialogVisible: Boolean = false,
) {
    val formEnabled: Boolean = !savingProfile

    val saveEnabled: Boolean = !isLoading
}
