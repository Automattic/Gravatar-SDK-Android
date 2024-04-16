package com.gravatar.ui.components.atomic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [Location] is a composable that displays a user's location in text format.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle Style to apply to the text
 * @param maxLines Maximum number of lines to display
 * @param dialogContent Content to display in a dialog when the text is clicked
 */
@Composable
public fun Location(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    maxLines: Int = 1,
    dialogContent: @Composable ((String) -> Unit)? = { DefaultDialogContent(text = it) },
) {
    ExpandableText(
        text = profile.currentLocation.orEmpty(),
        modifier = modifier,
        textStyle = textStyle,
        maxLines = maxLines,
        dialogContent = dialogContent,
    )
}

@Preview
@Composable
private fun LocationPreview() {
    Location(UserProfile("", currentLocation = "Crac'h, France"))
}
