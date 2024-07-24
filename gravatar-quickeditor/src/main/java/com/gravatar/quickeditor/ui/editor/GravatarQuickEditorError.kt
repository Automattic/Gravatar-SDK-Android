package com.gravatar.quickeditor.ui.editor

/**
 * Class representing possible errors that can occur when using the GravatarQuickEditor
 */
public sealed class GravatarQuickEditorError {
    /**
     * Any issue related to the OAuthFlow.
     * One of the reasons for the flow to fail are the wrong OAuthParams set in the GravatarQuickEditor
     */
    public data object OauthFailed : GravatarQuickEditorError()

    /**
     * This error will be used when an invalid/expired token is provided in the GravatarQuickEditor
     */
    public data object InvalidToken : GravatarQuickEditorError()
}
