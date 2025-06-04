package com.gravatar.quickeditor.ui.abouteditor

internal sealed class AboutEditorEvent {
    data class OnAboutFieldUpdated(
        val aboutField: AboutEditorField,
    ) : AboutEditorEvent()

    data object OnSaveClicked : AboutEditorEvent()

    data object HandleAuthFailureTapped : AboutEditorEvent()

    data object Refresh : AboutEditorEvent()

    data class OnCompactWindowEnabled(val enabled: Boolean) : AboutEditorEvent()
}
