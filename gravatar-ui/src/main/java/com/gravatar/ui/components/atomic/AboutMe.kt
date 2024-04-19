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
