package com.gravatar.ui.components.atomic

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [Location] is a composable that displays a user's location in text format.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user location
 */
@Composable
public fun Location(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    content(profile.currentLocation.orEmpty(), modifier)
}

@Composable
private fun LocationDefaultContent(location: String, textStyle: TextStyle, modifier: Modifier) = Text(
    location,
    modifier = modifier,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    style = textStyle,
)

@Preview
@Composable
private fun LocationPreview() {
    Location(UserProfile("", currentLocation = "Crac'h, France"))
}
