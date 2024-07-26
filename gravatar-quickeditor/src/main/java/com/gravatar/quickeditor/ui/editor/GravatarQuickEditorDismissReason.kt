package com.gravatar.quickeditor.ui.editor

/**
 * Class representing possible dismiss reasons that could close the GravatarQuickEditor
 */
public sealed class GravatarQuickEditorDismissReason {
    /**
     * Quick Editor was closed by user
     */
    public data object Finished : GravatarQuickEditorDismissReason()

    /**
     * Any issue related to the OAuthFlow.
     * One of the reasons for the flow to fail are the wrong OAuthParams set in the GravatarQuickEditor
     */
    public data object OauthFailed : GravatarQuickEditorDismissReason()

    /**
     * This reason will be used when an invalid/expired token is provided in the GravatarQuickEditor
     */
    public data object InvalidToken : GravatarQuickEditorDismissReason()
}
