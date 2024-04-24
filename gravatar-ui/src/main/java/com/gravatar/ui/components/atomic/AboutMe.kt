package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.LoadingToLoadedStatePreview
import com.gravatar.ui.components.UserProfileState

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@Composable
public fun AboutMe(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    content(profile.aboutMe.orEmpty(), modifier)
}

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param state The user's profile loading state
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@Composable
public fun AboutMe(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    when (state) {
        is UserProfileState.Loading -> {
            TextSkeletonEffect(textStyle = textStyle)
        }

        is UserProfileState.Loaded -> {
            AboutMe(state.userProfile, modifier, textStyle, content)
        }
    }
}

@Composable
private fun AboutMeDefaultContent(userInfo: String, textStyle: TextStyle, modifier: Modifier) = Text(
    userInfo,
    modifier = modifier,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    style = textStyle,
)

@Preview
@Composable
private fun AboutMePreview() {
    AboutMe(
        UserProfile(
            "",
            aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
        ),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedStatePreview { AboutMe(it) }
}
