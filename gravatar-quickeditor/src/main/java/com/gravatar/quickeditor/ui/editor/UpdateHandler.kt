package com.gravatar.quickeditor.ui.editor

import com.gravatar.restapi.models.Profile
import java.util.Objects

/**
 * Callback to handle updates from the Quick Editor.
 */
public typealias UpdateHandler = (QuickEditorUpdateType) -> Unit

/**
 * The interface for all Quick Editor result types.
 */
public interface QuickEditorUpdateType

/**
 * AvatarPicker result.
 */
public data object AvatarPickerResult : QuickEditorUpdateType

/**
 * AboutEditor result with the updated Profile in the payload.
 *
 * @property profile The updated Profile.
 */
public class AboutEditorResult(
    public val profile: Profile,
) : QuickEditorUpdateType {
    override fun toString(): String = "AboutPayload(profile=$profile)"

    override fun equals(other: Any?): Boolean = other is AboutEditorResult && other.profile == profile

    override fun hashCode(): Int = Objects.hash(profile)
}
