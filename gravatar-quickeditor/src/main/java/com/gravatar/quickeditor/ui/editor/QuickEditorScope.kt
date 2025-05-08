package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The scope of the Quick Editor. The scope corresponds to the part of your Gravatar profile that will be editable
 * in the Quick Editor.
 *
 * @property value The value of the scope.
 **/
@JvmInline
@Parcelize
public value class QuickEditorScope private constructor(
    public val value: String,
) : Parcelable {
    public companion object {
        /**
         * Avatar scope that will launch the Avatar Picker.
         */
        @JvmStatic
        public val Avatar: QuickEditorScope = QuickEditorScope("avatar")

        /**
         * About scope that will launch the About Editor.
         */
        @JvmStatic
        public val About: QuickEditorScope = QuickEditorScope("about")

        /**
         * Avatar and About scope that will launch both the Avatar Picker and About Editor.
         */
        @JvmStatic
        public val AvatarAndAbout: QuickEditorScope = QuickEditorScope("avatar_and_about")
    }
}
