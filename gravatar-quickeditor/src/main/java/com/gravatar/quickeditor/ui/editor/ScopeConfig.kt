package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects

/**
 * Scope configuration for the Quick Editor.
 * This class defines the scope of the Quick Editor and related parameters.
 *
 * @property scope The scope of the Quick Editor
 * @property avatarPickerContentLayout The content layout direction used in the Avatar Picker
 * @property initialPage The initial page to be shown in the Quick Editor
 * @property aboutInputFields The input fields to be shown in the About Editor
 */
@Parcelize
public class ScopeConfig private constructor(
    public val scope: QuickEditorScope,
    public val avatarPickerContentLayout: AvatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
    public val initialPage: QuickEditorPage = QuickEditorPage.AvatarPicker,
    public val aboutInputFields: Set<AboutInputField> = AboutInputField.all,
) : Parcelable {
    override fun toString(): String = "ScopeConfig(" +
        "scope=$scope, " +
        "avatarPickerContentLayout=$avatarPickerContentLayout, " +
        "initialPage=$initialPage, " +
        "aboutInputFields=$aboutInputFields" +
        ")"

    override fun hashCode(): Int = Objects.hash(scope, avatarPickerContentLayout, initialPage, aboutInputFields)

    override fun equals(other: Any?): Boolean {
        return other is ScopeConfig &&
            scope == other.scope &&
            avatarPickerContentLayout == other.avatarPickerContentLayout &&
            initialPage == other.initialPage &&
            aboutInputFields == other.aboutInputFields
    }

    /**
     * A type-safe builder for the ScopeConfig class.
     */
    public class Builder {
        /**
         * The scope of the Quick Editor
         */
        @set:JvmSynthetic
        public var scope: QuickEditorScope = QuickEditorScope.Avatar

        /**
         * The content layout direction used in the Avatar Picker
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var avatarPickerContentLayout: AvatarPickerContentLayout = AvatarPickerContentLayout.Horizontal

        /**
         * The initial page to be shown in the Quick Editor
         */
        @set:JvmSynthetic
        public var initialPage: QuickEditorPage = QuickEditorPage.AvatarPicker

        /**
         * The input fields to be shown in the About Editor
         */
        @set:JvmSynthetic
        public var aboutInputFields: Set<AboutInputField> = AboutInputField.all

        /**
         * Sets the content layout direction used in the Avatar Picker
         */
        public fun setAvatarPickerContentLayout(avatarPickerContentLayout: AvatarPickerContentLayout): Builder =
            apply { this.avatarPickerContentLayout = avatarPickerContentLayout }

        /**
         * Sets the scope
         */
        public fun setScope(scope: QuickEditorScope): Builder = apply { this.scope = scope }

        /**
         * Sets the initial page
         */
        public fun setInitialPage(initialPage: QuickEditorPage): Builder = apply { this.initialPage = initialPage }

        /**
         * Sets the input fields to be shown in the About Editor
         */
        public fun setAboutInputFields(aboutInputFields: Set<AboutInputField>): Builder =
            apply { this.aboutInputFields = aboutInputFields }

        /**
         * Builds the ScopeConfig object
         */
        public fun build(): ScopeConfig {
            return ScopeConfig(
                scope,
                avatarPickerContentLayout,
                scope.derivedInitialPage(initialPage),
                aboutInputFields,
            )
        }
    }

    public companion object {
        /**
         * Helper function to create a ScopeConfig for the Avatar scope.
         *
         * @param avatarPickerContentLayout The content layout direction used in the Avatar Picker
         * @return A ScopeConfig object for the Avatar scope
         */
        public fun avatar(
            avatarPickerContentLayout: AvatarPickerContentLayout =
                AvatarPickerContentLayout.Horizontal,
        ): ScopeConfig {
            return ScopeConfig(
                scope = QuickEditorScope.Avatar,
                avatarPickerContentLayout = avatarPickerContentLayout,
            )
        }

        /**
         * Helper function to create a ScopeConfig for the About scope.
         *
         * @param aboutInputFields The input fields to be shown in the About Editor
         * @return A ScopeConfig object for the About scope
         */
        public fun about(aboutInputFields: Set<AboutInputField> = AboutInputField.all): ScopeConfig {
            return ScopeConfig(
                scope = QuickEditorScope.About,
                aboutInputFields = aboutInputFields,
            )
        }

        /**
         * Helper function to create a ScopeConfig for the Avatar and About scope.
         *
         * @param avatarPickerContentLayout The content layout direction used in the Avatar Picker
         * @param initialPage The initial page to be shown in the Quick Editor
         * @param aboutInputFields The input fields to be shown in the About Editor
         * @return A ScopeConfig object for the Avatar and About scope
         */
        public fun avatarAndAbout(
            avatarPickerContentLayout: AvatarPickerContentLayout =
                AvatarPickerContentLayout.Horizontal,
            initialPage: QuickEditorPage = QuickEditorPage.AvatarPicker,
            aboutInputFields: Set<AboutInputField> = AboutInputField.all,
        ): ScopeConfig {
            return ScopeConfig(
                scope = QuickEditorScope.AvatarAndAbout,
                avatarPickerContentLayout = avatarPickerContentLayout,
                initialPage = initialPage,
                aboutInputFields = aboutInputFields,
            )
        }
    }
}

/**
 * A type-safe builder for the ScopeConfig class.
 *
 * @param initializer Function literal with ScopeConfig.Builder as the receiver
 */
@JvmSynthetic // Hide from Java callers who should use Builder.
public fun ScopeConfig(
    initializer: ScopeConfig.Builder.() -> Unit,
): ScopeConfig = ScopeConfig.Builder().apply(initializer).build()

/**
 * This function makes sure we only use the configured initial page for the scope where it can be configured.
 */
private fun QuickEditorScope.derivedInitialPage(page: QuickEditorPage): QuickEditorPage = when (this) {
    QuickEditorScope.AvatarAndAbout -> page
    QuickEditorScope.Avatar -> QuickEditorPage.AvatarPicker
    QuickEditorScope.About -> QuickEditorPage.AboutEditor
    else -> QuickEditorPage.AvatarPicker
}
