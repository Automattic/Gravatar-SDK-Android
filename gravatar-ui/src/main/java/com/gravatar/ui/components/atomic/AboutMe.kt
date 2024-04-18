package com.gravatar.ui.components.atomic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param content Composable to display the user's about me description
 */
@Composable
public fun AboutMe(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, contentModifier)
    },
) {
    content(profile.aboutMe.orEmpty(), modifier)
}

@Composable
private fun AboutMeDefaultContent(userInfo: String, modifier: Modifier) = Text(
    userInfo,
    modifier = modifier,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
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
