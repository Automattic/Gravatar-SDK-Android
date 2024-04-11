package com.gravatar.ui.components.atomic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param maxLines The maximum number of lines to display before truncating the text
 * @param dialogContent The content to display in a dialog when the truncated text is clicked
 */
@Composable
public fun AboutMe(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    dialogContent: @Composable ((String) -> Unit)? = { DefaultDialogContent(text = it) },
) {
    ExpandableText(profile.aboutMe.orEmpty(), modifier, maxLines, dialogContent)
}

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
