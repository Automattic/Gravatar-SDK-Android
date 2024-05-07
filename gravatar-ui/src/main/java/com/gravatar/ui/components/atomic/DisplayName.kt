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
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.R
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.LoadingToLoadedStatePreview
import com.gravatar.ui.components.UserProfileState

/**
 * [DisplayName] is a composable that displays the user's display name.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 */
@Composable
public fun DisplayName(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    DisplayName(profile.displayName.orEmpty(), modifier, textStyle)
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
@Composable
public fun DisplayName(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    when (state) {
        is UserProfileState.Loading -> {
            TextSkeletonEffect(textStyle = textStyle)
        }

        is UserProfileState.Loaded -> {
            DisplayName(state.userProfile, modifier, textStyle)
        }

        UserProfileState.Empty -> DisplayName(
            displayName = stringResource(id = R.string.empty_state_display_name),
            modifier,
            textStyle,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
public fun DisplayNamePreview() {
    LoadingToLoadedStatePreview { DisplayName(it) }
}
