package com.gravatar.ui.components.atomic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 * @param maxLines The maximum number of lines to display before truncating the text
 * @param dialogContent The content to display in a dialog when the truncated text is clicked
 */
@Composable
public fun AboutMe(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = 2,
    dialogContent: @Composable ((String) -> Unit)? = { DefaultDialogContent(text = it) },
) {
    ExpandableText(profile.aboutMe.orEmpty(), modifier, textStyle, maxLines, dialogContent)
}

@Preview(showBackground = true)
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
