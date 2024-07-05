package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import com.gravatar.api.models.Profile as LegacyProfile

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
    profile: Profile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    content(profile.location, modifier)
}

/**
 * [Location] is a composable that displays a user's location in text format or a loading skeleton.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param state The user's profile loading state
 * @param modifier Composable modifier
 * @param skeletonModifier Composable modifier for the loading skeleton component
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user location
 */
@JvmName("LocationWithComponentState")
@Composable
public fun Location(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    skeletonModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    when (state) {
        is ComponentState.Loading -> {
            TextSkeletonEffect(textStyle = textStyle, modifier = skeletonModifier)
        }

        is ComponentState.Loaded -> {
            if (state.loadedValue.location.isNotBlank()) {
                Location(state.loadedValue, modifier, textStyle, content)
            }
        }

        ComponentState.Empty -> {
            content.invoke(stringResource(id = R.string.empty_state_user_info), modifier)
        }
    }
}

/**
 * [Location] is a composable that displays a user's location in text format.
 * The user's location is displayed in a text format. If the location is too long, it will be truncated
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user location
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.Location"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun Location(
    profile: LegacyProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    Location(profile = profile.toApi2Profile(), modifier = modifier, textStyle = textStyle, content = content)
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
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.Location"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun Location(
    state: ComponentState<LegacyProfile>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { location, contentModifier ->
        LocationDefaultContent(location, textStyle, contentModifier)
    },
) {
    Location(
        state.toApi2ComponentStateProfile(),
        modifier,
        skeletonModifier = Modifier,
        textStyle = textStyle,
        content = content,
    )
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
    Location(defaultProfile("", location = "Crac'h, France"))
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedProfileStatePreview { Location(it) }
}
