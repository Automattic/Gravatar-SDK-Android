package com.gravatar.quickeditor

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.extensions.addQuickEditorToView
import com.gravatar.quickeditor.ui.oauth.OAuthParams

/**
 * Singleton object that provides easy to use functions to interact with the Gravatar Quick Editor.
 */
public object GravatarQuickEditor {
    /**
     * Helper function to launch the Gravatar Quick Editor from the activity.
     *
     * @param activity The activity to launch the Gravatar Quick Editor from.
     * @param appName Name of the app that is launching the Quick Editor
     * @param oAuthParams The parameters to configure the OAuth.
     * @param onAvatarSelected The callback for the avatar update result, check [AvatarUpdateResult].
     *                       Can be invoked multiple times while the Quick Editor is open.
     * @param onDismiss The callback for the dismiss action.
     *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
     */
    @JvmStatic
    public fun show(
        activity: Activity,
        appName: String,
        oAuthParams: OAuthParams,
        onAvatarSelected: (AvatarUpdateResult) -> Unit,
        onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
    ) {
        val viewGroup: ViewGroup = activity.findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, appName, oAuthParams, onAvatarSelected, onDismiss)
    }

    /**
     * Helper function to launch the Gravatar Quick Editor from the fragment. Internally it uses
     * `Activity.requireActivity()` to get the activity.
     *
     * @param fragment The fragment to launch the Gravatar Quick Editor from.
     * @param appName Name of the app that is launching the Quick Editor
     * @param oAuthParams The parameters to configure the OAuth.
     * @param onAvatarSelected The callback for the avatar update result, check [AvatarUpdateResult].
     *                       Can be invoked multiple times while the Quick Editor is open.
     * @param onDismiss The callback for the dismiss action.
     *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
     */
    @JvmStatic
    public fun show(
        fragment: Fragment,
        appName: String,
        oAuthParams: OAuthParams,
        onAvatarSelected: (AvatarUpdateResult) -> Unit,
        onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
    ) {
        val viewGroup: ViewGroup = fragment.requireActivity().findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, appName, oAuthParams, onAvatarSelected, onDismiss)
    }
}
