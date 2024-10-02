package com.gravatar.quickeditor.ui.editor

import com.gravatar.types.Email
import java.util.Objects

/**
 * All non-auth params required to launch the Gravatar Quick Editor
 *
 * @property email The email of the user
 * @property avatarPickerContentLayout The layout direction used in the Avatar Picker.
 */
public class GravatarQuickEditorParams private constructor(
    public val email: Email,
    public val avatarPickerContentLayout: AvatarPickerContentLayout,
) {
    override fun toString(): String =
        "GravatarQuickEditorParams(email='$email', avatarPickerContentLayout=$avatarPickerContentLayout)"

    override fun hashCode(): Int = Objects.hash(email, avatarPickerContentLayout)

    override fun equals(other: Any?): Boolean {
        return other is GravatarQuickEditorParams &&
            email == other.email &&
            avatarPickerContentLayout == other.avatarPickerContentLayout
    }

    /**
     * A type-safe builder for the GravatarQuickEditorParams class.
     */
    public class Builder {
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
         * Sets the email
         */
        public fun setEmail(email: Email): Builder = apply { this.email = email }

        /**
         * Builds the GravatarQuickEditorParams object
         */
        public fun build(): GravatarQuickEditorParams = GravatarQuickEditorParams(
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
