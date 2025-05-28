package com.gravatar.quickeditor.ui.abouteditor

import com.gravatar.quickeditor.ui.avatarpicker.SectionError

internal data class AboutEditorUiState(
    val aboutFields: Set<AboutEditorField> = emptySet(),
    val isLoading: Boolean = false,
    val savingProfile: Boolean = false,
    val discardChangesDialogVisible: Boolean = false,
    val error: SectionError? = null,
    val savedAboutFields: Set<AboutEditorField> = emptySet(),
    val compactWindow: Boolean = false,
) {
    val formEnabled: Boolean = !savingProfile

    val unsavedChanges: Boolean = aboutFields != savedAboutFields

    val saveEnabled: Boolean = !isLoading && unsavedChanges
}
