package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Configuration which will be applied to the avatar picker.
 *
 * @property contentLayout The layout direction of the Avatar picker in the Quick Editor.
 */
@Parcelize
public class AvatarPickerConfiguration(
    public val contentLayout: AvatarPickerContentLayout,
) : Parcelable {
    internal companion object {
        val default = AvatarPickerConfiguration(
            contentLayout = AvatarPickerContentLayout.Horizontal,
        )
    }
}

/**
 * Configuration which will be applied to the about editor.
 *
 * @property fields The input fields to be shown in the about editor.
 */
@Parcelize
public class AboutEditorConfiguration(
    public val fields: Set<AboutInputField>,
) : Parcelable {
    internal companion object {
        val default = AboutEditorConfiguration(
            fields = AboutInputField.all,
        )
    }
}

/**
 * Configuration which will be applied to the avatar picker and about editor.
 *
 * @property contentLayout The layout direction of the Avatar picker in the Quick Editor.
 * @property fields The input fields to be shown in the about editor.
 * @property initialPage The initial page to be shown in the Quick Editor.
 */
@Parcelize
public class AvatarPickerAndAboutEditorConfiguration(
    public val contentLayout: AvatarPickerContentLayout,
    public val fields: Set<AboutInputField>,
    public val initialPage: QuickEditorPage,
) : Parcelable {
    internal companion object {
        val default = AvatarPickerAndAboutEditorConfiguration(
            contentLayout = AvatarPickerContentLayout.Horizontal,
            fields = AboutInputField.all,
            initialPage = QuickEditorPage.AvatarPicker,
        )
    }
}
