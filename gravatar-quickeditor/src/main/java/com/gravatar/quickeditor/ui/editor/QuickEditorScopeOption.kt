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

    override fun toString(): String = "QuickEditorScopeOption(scope=$scope)"

    override fun hashCode(): Int = Objects.hash(scope)

    override fun equals(other: Any?): Boolean {
        return other is QuickEditorScopeOption &&
            scope == other.scope
    }

    internal val avatarPickerContentLayout: AvatarPickerContentLayout
        get() = when (scope) {
            is Scope.AvatarPicker -> scope.config.contentLayout
            is Scope.AvatarPickerAndAboutEditor -> scope.config.contentLayout
            is Scope.AboutEditor -> AvatarPickerContentLayout.Horizontal
        }

    internal val aboutFields: Set<AboutInputField>
        get() = when (scope) {
            is Scope.AboutEditor -> scope.config.fields
            is Scope.AvatarPickerAndAboutEditor -> scope.config.fields
            is Scope.AvatarPicker -> AboutInputField.all
        }

    internal val initialPage: QuickEditorPage
        get() = when (scope) {
            is Scope.AvatarPicker -> QuickEditorPage.AvatarPicker
            is Scope.AboutEditor -> QuickEditorPage.AboutEditor
            is Scope.AvatarPickerAndAboutEditor -> scope.config.initialPage.internalType
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

        /**
         * Creates a `QuickEditorScopeOption` configured for the avatar picker scope using a DSL-style builder.
         *
         * @param initializer Function literal with AvatarPickerConfiguration.Builder as the receiver
         * @return A configured instance of `QuickEditorScopeOption` for the avatar picker scope
         */
        @JvmSynthetic // Hide from Java callers who should use the static method.
        public fun avatarPicker(
            initializer: AvatarPickerConfiguration.Builder.() -> Unit,
        ): QuickEditorScopeOption {
            return avatarPicker(AvatarPickerConfiguration.Builder().apply(initializer).build())
        }

        /**
         * Creates a `QuickEditorScopeOption` configured for the about editor scope using a DSL-style builder.
         *
         * @param initializer Function literal with AboutEditorConfiguration.Builder as the receiver
         * @return A configured instance of `QuickEditorScopeOption` for the about editor scope
         */
        @JvmSynthetic // Hide from Java callers who should use the static method.
        public fun aboutEditor(
            initializer: AboutEditorConfiguration.Builder.() -> Unit,
        ): QuickEditorScopeOption {
            return aboutEditor(AboutEditorConfiguration.Builder().apply(initializer).build())
        }

        /**
         * Creates a `QuickEditorScopeOption` configured for the avatar and about editor scope
         * using a DSL-style builder.
         *
         * @param initializer Function literal with AvatarPickerAndAboutEditorConfiguration.Builder as the receiver
         * @return A configured instance of `QuickEditorScopeOption` for the avatar and about editor scope
         */
        @JvmSynthetic // Hide from Java callers who should use the static method.
        public fun avatarAndAbout(
            initializer: AvatarPickerAndAboutEditorConfiguration.Builder.() -> Unit,
        ): QuickEditorScopeOption {
            return avatarAndAbout(AvatarPickerAndAboutEditorConfiguration.Builder().apply(initializer).build())
        }
    }
}
