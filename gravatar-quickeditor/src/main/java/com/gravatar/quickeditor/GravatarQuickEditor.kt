package com.gravatar.quickeditor

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.extensions.addQuickEditorToView
import com.gravatar.types.Email

/**
 * Singleton object that provides easy to use functions to interact with the Gravatar Quick Editor.
 */
public object GravatarQuickEditor {
    /**
     * Helper function to launch the Gravatar Quick Editor from the activity.
     *
     * @param activity The activity to launch the Gravatar Quick Editor from.
     * @param gravatarQuickEditorParams The parameters to configure the Quick Editor.
     * @param authenticationMethod The method used for authentication with the Gravatar REST API.
     * @param onAvatarSelected The callback for the avatar update.
     *                       Can be invoked multiple times while the Quick Editor is open.
     * @param onDismiss The callback for the dismiss action containing [GravatarQuickEditorDismissReason]
     */
    @JvmStatic
    @JvmOverloads
    public fun show(
        activity: Activity,
        gravatarQuickEditorParams: GravatarQuickEditorParams,
        authenticationMethod: AuthenticationMethod,
        onAvatarSelected: () -> Unit,
        onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    ) {
        val viewGroup: ViewGroup = activity.findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, gravatarQuickEditorParams, authenticationMethod, onAvatarSelected, onDismiss)
    }

    /**
     * Helper function to launch the Gravatar Quick Editor from the fragment. Internally it uses
     * `Activity.requireActivity()` to get the activity.
     *
     * @param fragment The fragment to launch the Gravatar Quick Editor from.
     * @param gravatarQuickEditorParams The parameters to configure the Quick Editor.
     * @param authenticationMethod The method used for authentication with the Gravatar REST API.
     * @param onAvatarSelected The callback for the avatar update.
     *                       Can be invoked multiple times while the Quick Editor is open.
     * @param onDismiss The callback for the dismiss action containing [GravatarQuickEditorDismissReason]
     */
    @JvmStatic
    @JvmOverloads
    public fun show(
        fragment: Fragment,
        gravatarQuickEditorParams: GravatarQuickEditorParams,
        authenticationMethod: AuthenticationMethod,
        onAvatarSelected: () -> Unit,
        onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    ) {
        val viewGroup: ViewGroup = fragment.requireActivity().findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, gravatarQuickEditorParams, authenticationMethod, onAvatarSelected, onDismiss)
    }

    /**
     * Function to remove the stored token. This function should be invoked
     * when the user logs out from your app.
     *
     * Once this is called, the user will be logged out and will have to go through the authentication flow again.
     *
     * @param email The email of the user.
     */
    public suspend fun logout(email: Email) {
        QuickEditorContainer.getInstance().dataStoreTokenStorage.deleteToken(email.hash().toString())
    }
}
