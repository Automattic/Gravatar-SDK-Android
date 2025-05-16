package com.gravatar.quickeditor.ui.abouteditor

import com.gravatar.quickeditor.ui.editor.AboutInputField

internal data class AboutEditorField(
    val type: AboutInputField,
    val value: String,
    val maxLines: Int = 1,
)
