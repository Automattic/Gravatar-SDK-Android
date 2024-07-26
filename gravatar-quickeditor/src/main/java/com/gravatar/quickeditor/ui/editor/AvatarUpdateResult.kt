package com.gravatar.quickeditor.ui.editor

import android.net.Uri
import java.util.Objects

/**
 * Payload with the information about the updated avatar
 *
 * @property avatarUri The URI of the selected avatar
 */
public class AvatarUpdateResult(
    public val avatarUri: Uri,
) {
    override fun toString(): String = "AvatarUpdateResult(avatarUri=$avatarUri)"

    override fun hashCode(): Int = Objects.hash(avatarUri)

    override fun equals(other: Any?): Boolean = other is AvatarUpdateResult && other.avatarUri == avatarUri
}
