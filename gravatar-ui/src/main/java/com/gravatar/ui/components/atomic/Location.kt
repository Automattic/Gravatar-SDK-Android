package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.LoadingToLoadedStatePreview
import com.gravatar.ui.components.UserProfileLoadingState

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

/**
 * [Location] is a composable that displays a user's location in text format or a loading skeleton.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param state The user's profile loading state
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user location
 */
@Composable
public fun Location(
    state: UserProfileLoadingState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    when (state) {
        is UserProfileLoadingState.Loading -> {
            TextSkeletonEffect(textStyle = textStyle, modifier = Modifier.width(120.dp))
        }

        is UserProfileLoadingState.Loaded -> {
            Location(state.userProfile, modifier, textStyle, content)
        }
    }
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedStatePreview { Location(it) }
}
