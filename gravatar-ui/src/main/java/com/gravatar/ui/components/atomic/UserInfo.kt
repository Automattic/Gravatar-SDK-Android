package com.gravatar.ui.components.atomic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile
import com.gravatar.extensions.formattedUserInfo

/**
 * [UserInfo] is a composable that displays a user's information in a formatted way.
 * The user's information includes their company, job title, pronunciation, pronouns, and current
 * location when available.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 * @param maxLines The maximum number of lines to display before truncating the text
 * @param dialogContent The content to display in a dialog when the truncated text is clicked
 */
@Composable
public fun UserInfo(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    maxLines: Int = 2,
    dialogContent: @Composable ((String) -> Unit)? = { DefaultDialogContent(text = it) },
) {
    // TODO this doesn't work with one Text field due. If the job_title and the company line is too long,
    // it will to break the layout
    ExpandableText(
        text = profile.formattedUserInfo(),
        modifier = modifier,
        textStyle = textStyle,
        maxLines = maxLines,
        dialogContent = dialogContent,
    )
}

@Preview(showBackground = true)
@Composable
private fun UserInfoPreview() {
    UserInfo(
        UserProfile(
            "",
            currentLocation = "Crac'h, France",
            pronouns = "They/Them",
            pronunciation = "Tony with a P",
            jobTitle = "Pony Trainer",
            company = "Pony Land",
        ),
    )
}
