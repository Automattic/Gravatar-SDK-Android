package com.gravatar.ui.components.atomic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile
import com.gravatar.extensions.formattedUserInfo

@Composable
public fun UserInfo(profile: UserProfile, modifier: Modifier = Modifier, maxLines: Int = 2) {
    // TODO this doesn't work with one Text field due. If the job_title and the company line is too long,
    // it will to break the layout
    ExpandableText(profile.formattedUserInfo(), modifier, maxLines)
}

@Preview
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
