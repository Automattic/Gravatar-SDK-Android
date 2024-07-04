package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import com.gravatar.api.models.Profile as LegacyProfile

/**
 * [DisplayName] is a composable that displays the user's display name.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.DisplayName"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun DisplayName(
    profile: LegacyProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    DisplayName(profile = profile.toApi2Profile(), modifier = modifier, textStyle = textStyle)
}

/**
 * [DisplayName] is a composable that displays the user's display name.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 */
@Composable
public fun DisplayName(
    profile: Profile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    DisplayName(profile.displayName, modifier, textStyle)
}

@Composable
private fun DisplayName(displayName: String, modifier: Modifier, textStyle: TextStyle) {
    Text(text = displayName, modifier = modifier, style = textStyle)
}

/**
 * [DisplayName] is a composable that displays the user's display name or a loading skeleton.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.DisplayName"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun DisplayName(
    state: ComponentState<LegacyProfile>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    DisplayName(
        state = state.toApi2ComponentStateProfile(),
        modifier = modifier,
        skeletonModifier = Modifier,
        textStyle = textStyle,
    )
}

/**
 * [DisplayName] is a composable that displays the user's display name or a loading skeleton.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param skeletonModifier Composable modifier for the loading skeleton component
 * @param textStyle The style to apply to the text
 */
@JvmName("DisplayNameWithComponentState")
@Composable
public fun DisplayName(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    skeletonModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    when (state) {
        is ComponentState.Loading -> {
            TextSkeletonEffect(
                textStyle = textStyle,
                modifier = skeletonModifier,
                skeletonVerticalPadding = 4.dp,
            )
        }

        is ComponentState.Loaded -> {
            DisplayName(state.loadedValue, modifier, textStyle)
        }

        ComponentState.Empty -> DisplayName(
            displayName = stringResource(id = R.string.empty_state_display_name),
            modifier,
            textStyle,
        )
    }
}

// TODO Make this preview internal in a future major release
@Deprecated(
    "This function is deprecated and will be removed in a future release.",
    level = DeprecationLevel.WARNING,
)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
public fun DisplayNamePreview() {
    LoadingToLoadedProfileStatePreview { DisplayName(it) }
}
