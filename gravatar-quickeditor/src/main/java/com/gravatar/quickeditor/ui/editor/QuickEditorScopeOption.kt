package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects

/**
 * Represents a profile editing scope with configuration options for each scope.
 *
 * @property scope The scope of the Quick Editor
 */
@Parcelize
public class QuickEditorScopeOption private constructor(
    internal val scope: Scope,
) : Parcelable {
    internal sealed class Scope : Parcelable {
        @Parcelize
        data class AvatarPicker(val config: AvatarPickerConfiguration) : Scope()

        @Parcelize
        data class AboutEditor(val config: AboutEditorConfiguration) : Scope()

        @Parcelize
        data class AvatarPickerAndAboutEditor(val config: AvatarPickerAndAboutEditorConfiguration) : Scope()
    }

    override fun toString(): String = "ScopeConfig(scope=$scope)"

    override fun hashCode(): Int = Objects.hash(scope)

    override fun equals(other: Any?): Boolean {
        return other is QuickEditorScopeOption &&
            scope == other.scope
    }

    internal val initialPage: QuickEditorPage
        get() = when (scope) {
            is Scope.AvatarPicker -> QuickEditorPage.AvatarPicker
            is Scope.AboutEditor -> QuickEditorPage.AboutEditor
            is Scope.AvatarPickerAndAboutEditor -> scope.config.initialPage
        }

    internal val avatarPickerContentLayout: AvatarPickerContentLayout
        get() = when (scope) {
            is Scope.AvatarPicker -> scope.config.contentLayout
            is Scope.AvatarPickerAndAboutEditor -> scope.config.contentLayout
            is Scope.AboutEditor -> AvatarPickerContentLayout.Horizontal
        }

    public companion object {
        internal val default = QuickEditorScopeOption(
            scope = Scope.AvatarPicker(AvatarPickerConfiguration.default),
        )

        /**
         * Creates a `QuickEditorScopeOption` configured for the avatar picker scope.
         *
         * @param config The configuration for the Avatar Picker
         * @return A configured instance of `QuickEditorScopeOption` for the avatar picker scope
         */
        @JvmStatic
        public fun avatarPicker(
            config: AvatarPickerConfiguration = AvatarPickerConfiguration.default,
        ): QuickEditorScopeOption {
            return QuickEditorScopeOption(
                scope = Scope.AvatarPicker(config),
            )
        }

        /**
         * Creates a `QuickEditorScopeOption` configured for the about editor scope.
         *
         * @param config The configuration for the About Editor
         * @return A configured instance of `QuickEditorScopeOption` for the about editor scope
         */
        @JvmStatic
        public fun aboutEditor(
            config: AboutEditorConfiguration = AboutEditorConfiguration.default,
        ): QuickEditorScopeOption {
            return QuickEditorScopeOption(
                scope = Scope.AboutEditor(config),
            )
        }

        /**
         * Creates a `QuickEditorScopeOption` configured for the avatar and about editor scope.
         *
         * @param config The configuration for the Avatar and About Editor
         * @return A configured instance of `QuickEditorScopeOption` for the avatar and about editor scope
         */
        @JvmStatic
        public fun avatarAndAbout(
            config: AvatarPickerAndAboutEditorConfiguration = AvatarPickerAndAboutEditorConfiguration.default,
        ): QuickEditorScopeOption {
            return QuickEditorScopeOption(
                scope = Scope.AvatarPickerAndAboutEditor(config),
            )
        }
    }
}
