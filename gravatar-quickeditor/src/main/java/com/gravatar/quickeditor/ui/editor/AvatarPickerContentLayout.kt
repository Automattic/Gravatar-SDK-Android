package com.gravatar.quickeditor.ui.editor

/**
 * The layout direction of the Avatar picker in the Quick Editor.
 */
public sealed class AvatarPickerContentLayout {
    /**
     * Horizontal scrolling for the Avatars.
     */
    public data object Horizontal : AvatarPickerContentLayout()

    /**
     * Vertical scrolling for the Avatars.
     */
    public data object Vertical : AvatarPickerContentLayout()
}
