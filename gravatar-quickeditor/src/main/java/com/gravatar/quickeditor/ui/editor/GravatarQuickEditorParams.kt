package com.gravatar.quickeditor.ui.editor

import com.gravatar.types.Email
import java.util.Objects

/**
 * All non-auth params required to launch the Gravatar Quick Editor
 *
 * @property appName The appName of the app that is launching the Quick Editor
 * @property email The email of the user
 * @property avatarPickerContentLayout The layout direction used in the Avatar Picker.
 */
public class GravatarQuickEditorParams private constructor(
    public val appName: String,
    public val email: Email,
    public val avatarPickerContentLayout: AvatarPickerContentLayout,
) {
    override fun toString(): String = "GravatarQuickEditorParams(appName='$appName', email='$email')"

    override fun hashCode(): Int = Objects.hash(appName, email)

    override fun equals(other: Any?): Boolean {
        return other is GravatarQuickEditorParams &&
            appName == other.appName &&
            email == other.email
    }

    /**
     * A type-safe builder for the GravatarQuickEditorParams class.
     */
    public class Builder {
        /**
         *  The appName of the app that is launching the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var appName: String? = null

        /**
         * The email of the user
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var email: Email? = null

        /**
         * The content layout direction used in the Avatar Picker
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var avatarPickerContentLayout: AvatarPickerContentLayout = AvatarPickerContentLayout.Horizontal

        /**
         * Sets the content layout direction used in the Avatar Picker
         */
        public fun setAvatarPickerContentLayout(avatarPickerContentLayout: AvatarPickerContentLayout): Builder =
            apply { this.avatarPickerContentLayout = avatarPickerContentLayout }

        /**
         * Sets the appName
         */
        public fun setAppName(appName: String): Builder = apply { this.appName = appName }

        /**
         * Sets the email
         */
        public fun setEmail(email: Email): Builder = apply { this.email = email }

        /**
         * Builds the GravatarQuickEditorParams object
         */
        public fun build(): GravatarQuickEditorParams = GravatarQuickEditorParams(
            appName!!,
            email!!,
            avatarPickerContentLayout,
        )
    }
}

/**
 * A type-safe builder for the GravatarQuickEditorParams class.
 *
 * @param initializer Function literal with GravatarQuickEditorParams.Builder as the receiver
 */
@JvmSynthetic // Hide from Java callers who should use Builder.
public fun GravatarQuickEditorParams(
    initializer: GravatarQuickEditorParams.Builder.() -> Unit,
): GravatarQuickEditorParams = GravatarQuickEditorParams.Builder().apply(initializer).build()
