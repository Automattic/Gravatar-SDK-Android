package com.gravatar.quickeditor

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.extensions.addQuickEditorToView

/**
 * Singleton object that provides easy to use functions to interact with the Gravatar Quick Editor.
 */
public object GravatarQuickEditor {
    /**
     * Helper function to launch the Gravatar Quick Editor from the activity.
     *
     * @param activity The activity to launch the Gravatar Quick Editor from.
     * @param gravatarQuickEditorParams The parameters to configure the Gravatar Quick Editor.
     * @param onDismiss The callback to execute when the Gravatar Quick Editor is dismissed.
     */
    @JvmStatic
    public fun show(activity: Activity, gravatarQuickEditorParams: GravatarQuickEditorParams, onDismiss: () -> Unit) {
        val viewGroup: ViewGroup = activity.findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, gravatarQuickEditorParams, onDismiss)
    }

    /**
     * Helper function to launch the Gravatar Quick Editor from the fragment. Internally it uses
     * `Activity.requireActivity()` to get the activity.
     *
     * @param fragment The fragment to launch the Gravatar Quick Editor from.
     * @param gravatarQuickEditorParams The parameters to configure the Gravatar Quick Editor.
     * @param onDismiss The callback to execute when the Gravatar Quick Editor is dismissed.
     */
    @JvmStatic
    public fun show(fragment: Fragment, gravatarQuickEditorParams: GravatarQuickEditorParams, onDismiss: () -> Unit) {
        val viewGroup: ViewGroup = fragment.requireActivity().findViewById(android.R.id.content)
        addQuickEditorToView(viewGroup, gravatarQuickEditorParams, onDismiss)
    }
}
