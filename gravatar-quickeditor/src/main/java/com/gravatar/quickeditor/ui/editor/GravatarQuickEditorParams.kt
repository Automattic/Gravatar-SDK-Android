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
 * @property scopeConfig The scope configuration for the Quick Editor.
 */
@Parcelize
public class GravatarQuickEditorParams private constructor(
    public val email: Email,
    public val avatarPickerContentLayout: AvatarPickerContentLayout,
    public val uiMode: GravatarUiMode = GravatarUiMode.SYSTEM,
    public val scopeConfig: ScopeConfig = ScopeConfig.avatar(),
) : Parcelable {
    override fun toString(): String = "GravatarQuickEditorParams(" +
        "email='$email', " +
        "avatarPickerContentLayout=$avatarPickerContentLayout, " +
        "uiMode=$uiMode, " +
        "scope=$scopeConfig" +
        ")"

    override fun hashCode(): Int = Objects.hash(email, avatarPickerContentLayout, uiMode, scopeConfig)

    override fun equals(other: Any?): Boolean {
        return other is GravatarQuickEditorParams &&
            email == other.email &&
            avatarPickerContentLayout == other.avatarPickerContentLayout &&
            uiMode == other.uiMode &&
            scopeConfig == other.scopeConfig
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
        @Deprecated(message = "AvatarPickerContentLayout was moved to ScopeConfig")
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var avatarPickerContentLayout: AvatarPickerContentLayout = AvatarPickerContentLayout.Horizontal

        /**
         * The UI mode to be used in the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var uiMode: GravatarUiMode = GravatarUiMode.SYSTEM

        /**
         * The scope configuration for the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var scopeConfig: ScopeConfig = ScopeConfig.avatar()

        /**
         * Sets the content layout direction used in the Avatar Picker
         */
        @Deprecated(
            message = "AvatarPickerContentLayout was moved to ScopeConfig",
            replaceWith = ReplaceWith("setScopeConfig(scopeConfig)"),
        )
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
        public fun setScopeConfig(scopeConfig: ScopeConfig): Builder = apply { this.scopeConfig = scopeConfig }

        /**
         * Builds the GravatarQuickEditorParams object
         */
        public fun build(): GravatarQuickEditorParams = GravatarQuickEditorParams(
            email!!,
            avatarPickerContentLayout,
            uiMode,
            scopeConfig.withContentLayout(avatarPickerContentLayout),
        )

        private fun ScopeConfig.withContentLayout(
            deprecatedAvatarPickerContentLayout: AvatarPickerContentLayout,
        ): ScopeConfig {
            // AvatarPickerContentLayout.Vertical means non-default value was set so we should make sure to use it
            // for backwards compatibility
            val contentLayout = if (deprecatedAvatarPickerContentLayout != AvatarPickerContentLayout.Horizontal) {
                deprecatedAvatarPickerContentLayout
            } else {
                avatarPickerContentLayout
            }
            return ScopeConfig {
                scope = this@withContentLayout.scope
                initialPage = this@withContentLayout.initialPage
                avatarPickerContentLayout = contentLayout
            }
        }
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
