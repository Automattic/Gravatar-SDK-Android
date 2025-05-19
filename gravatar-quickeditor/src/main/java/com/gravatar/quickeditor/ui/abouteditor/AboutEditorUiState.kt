package com.gravatar.quickeditor.ui.abouteditor

import com.gravatar.quickeditor.ui.avatarpicker.SectionError

internal data class AboutEditorUiState(
    val aboutFields: Set<AboutEditorField> = emptySet(),
    val isLoading: Boolean = false,
    val savingProfile: Boolean = false,
    val discardChangesDialogVisible: Boolean = false,
    val error: SectionError? = null,
) {
    val formEnabled: Boolean = !savingProfile

    val saveEnabled: Boolean = !isLoading
}
