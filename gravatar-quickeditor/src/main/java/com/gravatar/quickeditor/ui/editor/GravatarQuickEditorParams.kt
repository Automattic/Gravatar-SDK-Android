package com.gravatar.quickeditor.ui.editor

import com.gravatar.quickeditor.ui.oauth.OAuthParams
import java.util.Objects

/**
 * Quick Editor's configuration parameters
 *
 * @property appName The App Name that is launching the Gravatar Quick Editor
 * @property oAuthParams The OAuth parameters for your WP.com application
 * @property scope The scope of the Quick Editor
 */
public class GravatarQuickEditorParams private constructor(
    public val appName: String,
    public val oAuthParams: OAuthParams,
    public val scope: GravatarQuickEditorScope,
) {

    override fun toString(): String =
        "ProfileQuickEditorParams(appName=$appName, oAuthParams=$oAuthParams, scope=$scope)"

    override fun hashCode(): Int = Objects.hash(appName, oAuthParams, scope)

    override fun equals(other: Any?): Boolean {
        return other is GravatarQuickEditorParams &&
            appName == other.appName &&
            oAuthParams == other.oAuthParams &&
            scope == other.scope
    }

    /**
     * A type-safe builder for the GravatarQuickEditorParams class.
     */
    public class Builder {
        /**
         * The App Name that is launching the Gravatar Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var appName: String? = null

        /**
         * The OAuth parameters for your WP.com application
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var oAuthParams: OAuthParams? = null

        /**
         * The scope of the Quick Editor
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var scope: GravatarQuickEditorScope? = null

        /**
         * Sets the App Name that is launching the Gravatar Quick Editor
         */
        public fun appName(appName: String): Builder = apply { this.appName = appName }

        /**
         * Sets the OAuth parameters for your WP.com application
         */
        public fun oAuthParams(oAuthParams: OAuthParams): Builder = apply { this.oAuthParams = oAuthParams }

        /**
         * Sets the scope of the Quick Editor
         */
        public fun scope(scope: GravatarQuickEditorScope): Builder = apply { this.scope = scope }

        /**
         * Builds the GravatarQuickEditorParams object
         */
        public fun build(): GravatarQuickEditorParams = GravatarQuickEditorParams(appName!!, oAuthParams!!, scope!!)
    }
}

/**
 * A type-safe builder for the GravatarQuickEditorParams class.
 *
 * @param initializer Function literal with GravatarQuickEditorParams.Builder as the receiver
 */
@JvmSynthetic
public fun GravatarQuickEditorParams(
    initializer: GravatarQuickEditorParams.Builder.() -> Unit,
): GravatarQuickEditorParams = GravatarQuickEditorParams.Builder().apply(initializer).build()
