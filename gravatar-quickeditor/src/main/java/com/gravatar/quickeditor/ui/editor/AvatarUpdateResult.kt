package com.gravatar.quickeditor.ui.editor

import android.net.Uri
import java.util.Objects

/**
 * All possible results for the Avatar update flow
 */
public sealed class AvatarUpdateResult {
    /**
     * Quick Editor dismissed without any errors or updates to the Gravatar
     */
    public data object Dismissed : AvatarUpdateResult() {
        override fun toString(): String = "AvatarUpdateResult.Dismissed"
    }

    /**
     * The Gravatar avatar has been updated
     *
     * @property avatarUri The URI of the selected avatar
     */
    public class Ok(public val avatarUri: Uri) : AvatarUpdateResult() {
        override fun toString(): String = "AvatarUpdateResult.Ok(avatarUri=$avatarUri)"

        override fun hashCode(): Int = Objects.hash(avatarUri)

        override fun equals(other: Any?): Boolean = other is Ok && other.avatarUri == avatarUri
    }

    /**
     * A [GravatarQuickEditorError] occurred while updating the Gravatar
     *
     * @property error The error that occurred
     */
    public class Error(public val error: GravatarQuickEditorError) : AvatarUpdateResult() {
        override fun toString(): String = "AvatarUpdateResult.Error(error=$error)"

        override fun hashCode(): Int = Objects.hash(error)

        override fun equals(other: Any?): Boolean = other is Error && other.error == error
    }
}
