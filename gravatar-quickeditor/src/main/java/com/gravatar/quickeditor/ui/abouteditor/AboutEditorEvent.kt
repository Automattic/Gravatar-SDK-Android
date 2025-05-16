package com.gravatar.quickeditor.ui.abouteditor

internal sealed class AboutEditorEvent {
    data class OnAboutFieldUpdated(
        val aboutField: AboutInputField,
    ) : AboutEditorEvent()

    data object OnSaveClicked : AboutEditorEvent()

    data object OnDoneClicked : AboutEditorEvent()

    data object OnUnsavedChangesKeepEditingClicked : AboutEditorEvent()

    data object OnUnsavedChangesExitClicked : AboutEditorEvent()
}
