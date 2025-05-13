package com.gravatar.quickeditor.ui.editor

import com.gravatar.restapi.models.Profile
import java.util.Objects

/**
 * This callback is invoked whenever the Quick Editor produces a result. The result is represented
 * by a `QuickEditorUpdateType` instance, which can be one of several variants. Developers should
 * implement this callback to handle the specific result types appropriately.
 *
 * Expected use cases:
 * - `AvatarPickerResult`: Indicates that the user has updated their avatar. This result does not
 *   carry any additional data and serves as a signal to refresh the avatar display.
 * - `AboutEditorResult`: Contains an updated `Profile` object after the user edits their "About Me"
 *   section. This result should be used to update the user's profile information in the application.
 *
 * Example usage:
 * ```
 * val updateHandler: UpdateHandler = { result ->
 *     when (result) {
 *         is AvatarPickerResult -> refreshAvatar()
 *         is AboutEditorResult -> updateProfile(result.profile)
 *     }
 * }
 * ```
 */
public typealias UpdateHandler = (QuickEditorUpdateType) -> Unit

/**
 * The interface for all Quick Editor result types.
 */
public interface QuickEditorUpdateType

/**
 * AvatarPicker result.
 *
 * This result type is used when the user updates their avatar. It does not contain any additional
 * data but serves as a signal to refresh the avatar display in the application.
 */
public data object AvatarPickerResult : QuickEditorUpdateType

/**
 * AboutEditor result with the updated Profile in the payload.
 *
 * This result type is used when the user edits their "About Me" section. It contains the updated
 * `Profile` object, which should be used to update the user's profile information in the application.
 *
 * @property profile The updated Profile object.
 */
public class AboutEditorResult(
    public val profile: Profile,
) : QuickEditorUpdateType {
    override fun toString(): String = "AboutPayload(profile=$profile)"

    override fun equals(other: Any?): Boolean = other is AboutEditorResult && other.profile == profile

    override fun hashCode(): Int = Objects.hash(profile)
}
