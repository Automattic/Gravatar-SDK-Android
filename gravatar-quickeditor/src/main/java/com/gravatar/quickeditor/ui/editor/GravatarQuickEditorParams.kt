package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import com.gravatar.types.Email
import kotlinx.parcelize.Parcelize
import java.util.Objects

/**
 * All non-auth params required to launch the Gravatar Quick Editor
 *
 * @property email The email of the user
 * @property avatarPickerContentLayout The layout direction used in the Avatar Picker.
 * @property uiMode The UI mode to be used in the Quick Editor.
 * @property scope The scope of the Quick Editor.
 */
@Parcelize
public class GravatarQuickEditorParams private constructor(
    public val email: Email,
    public val avatarPickerContentLayout: AvatarPickerContentLayout,
    public val uiMode: GravatarUiMode = GravatarUiMode.SYSTEM,
    public val scope: QuickEditorScope = QuickEditorScope.AVATAR,
) : Parcelable {
    override fun toString(): String = "GravatarQuickEditorParams(" +
        "email='$email', " +
        "avatarPickerContentLayout=$avatarPickerContentLayout, " +
        "uiMode=$uiMode, " +
        "scope=$scope" +
        ")"

    override fun hashCode(): Int = Objects.hash(email, avatarPickerContentLayout, uiMode, scope)

    override fun equals(other: Any?): Boolean {
        return other is GravatarQuickEditorParams &&
            email == other.email &&
            avatarPickerContentLayout == other.avatarPickerContentLayout &&
            uiMode == other.uiMode &&
            scope == other.scope
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
         * The UI mode to be used in the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var uiMode: GravatarUiMode = GravatarUiMode.SYSTEM

        /**
         * The scope of the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var scope: QuickEditorScope = QuickEditorScope.AVATAR

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
         * Sets the UI mode to be used in the Quick Editor
         */
        public fun setUiMode(uiMode: GravatarUiMode): Builder = apply { this.uiMode = uiMode }

        /**
         * Sets the scope of the Quick Editor
         */
        public fun setScope(scope: QuickEditorScope): Builder = apply { this.scope = scope }

        /**
         * Builds the GravatarQuickEditorParams object
         */
        public fun build(): GravatarQuickEditorParams = GravatarQuickEditorParams(
            email!!,
            avatarPickerContentLayout,
            uiMode,
            scope,
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
