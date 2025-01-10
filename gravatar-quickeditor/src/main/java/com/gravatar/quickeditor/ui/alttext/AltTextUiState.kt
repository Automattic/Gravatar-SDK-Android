package com.gravatar.quickeditor.ui.alttext

import java.net.URI

internal data class AltTextUiState(
    val avatarUrl: URI? = null,
    val isUpdating: Boolean,
    val altText: String = "",
    val altTextMaxLength: Int,
    private val initialAltText: String = altText,
) {
    val isSaveButtonEnabled: Boolean
        get() = altText != initialAltText && !isUpdating
}
