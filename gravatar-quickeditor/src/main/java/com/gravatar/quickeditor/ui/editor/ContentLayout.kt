package com.gravatar.quickeditor.ui.editor

/**
 * The contentLayout of the Avatar picker in the Quick Editor.
 */
public sealed class ContentLayout {
    /**
     * Horizontal scrolling for the Avatars.
     */
    public data object Horizontal : ContentLayout()

    /**
     * Vertical scrolling for the Avatars.
     */
    public data object Vertical : ContentLayout()
}
