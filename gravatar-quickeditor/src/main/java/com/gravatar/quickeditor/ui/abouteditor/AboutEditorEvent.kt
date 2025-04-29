package com.gravatar.quickeditor.ui.abouteditor

internal sealed class AboutEditorEvent {
    data class OnAboutFieldUpdated(
        val aboutField: AboutInputField,
    ) : AboutEditorEvent()
}
