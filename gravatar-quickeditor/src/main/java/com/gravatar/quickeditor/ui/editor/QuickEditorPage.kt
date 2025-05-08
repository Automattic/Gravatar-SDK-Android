package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The page of the Quick Editor. The page corresponds to the part of your Gravatar profile that will be shown.
 *
 * @property value The value of the page.
 */
@Parcelize
@JvmInline
public value class QuickEditorPage private constructor(
    public val value: String,
) : Parcelable {
    public companion object {
        /**
         * Avatar Picker page.
         */
        @JvmStatic
        public val AvatarPicker: QuickEditorPage =
            QuickEditorPage("avatar_picker")

        /**
         * About Editor page.
         */
        @JvmStatic
        public val AboutEditor: QuickEditorPage =
            QuickEditorPage("about_editor")
    }
}
