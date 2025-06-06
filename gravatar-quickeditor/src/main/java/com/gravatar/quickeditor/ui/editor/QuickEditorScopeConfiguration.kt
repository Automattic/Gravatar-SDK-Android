package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects

/**
 * Configuration which will be applied to the avatar picker.
 *
 * @property contentLayout The layout direction of the Avatar picker in the Quick Editor.
 */
@Parcelize
public class AvatarPickerConfiguration(
    public val contentLayout: AvatarPickerContentLayout,
) : Parcelable {
    override fun toString(): String = "AvatarPickerConfiguration(contentLayout=$contentLayout)"

    override fun hashCode(): Int = Objects.hash(contentLayout)

    override fun equals(other: Any?): Boolean = other is AvatarPickerConfiguration &&
        contentLayout == other.contentLayout

    /**
     * Builder class for AvatarPickerConfiguration.
     */
    public class Builder {
        /**
         * The layout direction of the Avatar picker in the Quick Editor.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var contentLayout: AvatarPickerContentLayout = default.contentLayout

        /**
         * Sets the layout direction of the Avatar picker in the Quick Editor.
         *
         * @param contentLayout The layout direction
         * @return This Builder instance
         */
        public fun setContentLayout(contentLayout: AvatarPickerContentLayout): Builder = apply {
            this.contentLayout = contentLayout
        }

        /**
         * Builds the AvatarPickerConfiguration.
         */
        public fun build(): AvatarPickerConfiguration = AvatarPickerConfiguration(contentLayout)
    }

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
    override fun toString(): String = "AboutEditorConfiguration(fields=$fields)"

    override fun hashCode(): Int = Objects.hash(fields)

    override fun equals(other: Any?): Boolean = other is AboutEditorConfiguration &&
        fields == other.fields

    /**
     * Builder class for AboutEditorConfiguration.
     */
    public class Builder {
        /**
         * The input fields to be shown in the about editor.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var fields: Set<AboutInputField> = default.fields

        /**
         * Sets the input fields to be shown in the about editor.
         *
         * @param fields The input fields
         * @return This Builder instance
         */
        public fun setFields(fields: Set<AboutInputField>): Builder = apply {
            this.fields = fields
        }

        /**
         * Builds the AboutEditorConfiguration.
         */
        public fun build(): AboutEditorConfiguration = AboutEditorConfiguration(fields)
    }

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
    public val initialPage: Page,
) : Parcelable {
    override fun toString(): String = "AvatarPickerAndAboutEditorConfiguration(" +
        "contentLayout=$contentLayout, " +
        "fields=$fields, " +
        "initialPage=$initialPage" +
        ")"

    override fun hashCode(): Int = Objects.hash(contentLayout, fields, initialPage)

    override fun equals(other: Any?): Boolean = other is AvatarPickerAndAboutEditorConfiguration &&
        contentLayout == other.contentLayout &&
        fields == other.fields &&
        initialPage == other.initialPage

    /**
     * Builder class for AvatarPickerAndAboutEditorConfiguration.
     */
    public class Builder {
        /**
         * The layout direction of the Avatar picker in the Quick Editor.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var contentLayout: AvatarPickerContentLayout = default.contentLayout

        /**
         * The input fields to be shown in the about editor.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var fields: Set<AboutInputField> = default.fields

        /**
         * The initial page to be shown in the Quick Editor.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var initialPage: Page = default.initialPage

        /**
         * Sets the layout direction of the Avatar picker in the Quick Editor.
         *
         * @param contentLayout The layout direction
         * @return This Builder instance
         */
        public fun setContentLayout(contentLayout: AvatarPickerContentLayout): Builder = apply {
            this.contentLayout = contentLayout
        }

        /**
         * Sets the input fields to be shown in the about editor.
         *
         * @param fields The input fields
         * @return This Builder instance
         */
        public fun setFields(fields: Set<AboutInputField>): Builder = apply {
            this.fields = fields
        }

        /**
         * Sets the initial page to be shown in the Quick Editor.
         *
         * @param initialPage The initial page
         * @return This Builder instance
         */
        public fun setInitialPage(initialPage: Page): Builder = apply {
            this.initialPage = initialPage
        }

        /**
         * Builds the AvatarPickerAndAboutEditorConfiguration.
         */
        public fun build(): AvatarPickerAndAboutEditorConfiguration = AvatarPickerAndAboutEditorConfiguration(
            contentLayout,
            fields,
            initialPage,
        )
    }

    /**
     * The page of the AvatarPickerAndAboutEditor scope.
     * The page corresponds to the part of your Gravatar profile that will be shown.
     *
     * @property value The value of the page.
     */
    @Parcelize
    @JvmInline
    public value class Page private constructor(
        public val value: String,
    ) : Parcelable {
        public companion object {
            /**
             * Avatar Picker page.
             */
            @JvmStatic
            public val AvatarPicker: Page = Page("avatar_picker")

            /**
             * About Editor page.
             */
            @JvmStatic
            public val AboutEditor: Page = Page("about_editor")
        }
    }

    internal companion object {
        val default = AvatarPickerAndAboutEditorConfiguration(
            contentLayout = AvatarPickerContentLayout.Horizontal,
            fields = AboutInputField.all,
            initialPage = Page.AvatarPicker,
        )
    }
}
