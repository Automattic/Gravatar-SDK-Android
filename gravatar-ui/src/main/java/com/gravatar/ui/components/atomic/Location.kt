package com.gravatar.ui.components.atomic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [Location] is a composable that displays a user's location in text format.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param maxLines Maximum number of lines to display
 * @param dialogContent Content to display in a dialog when the text is clicked
 */
@Composable
public fun Location(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    dialogContent: @Composable (() -> Unit)? = null,
) {
    ExpandableText(profile.currentLocation.orEmpty(), modifier, maxLines, dialogContent)
}

@Preview
@Composable
private fun LocationPreview() {
    Location(UserProfile("", currentLocation = "Crac'h, France"))
}
