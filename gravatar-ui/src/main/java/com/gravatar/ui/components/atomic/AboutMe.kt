package com.gravatar.ui.components.atomic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

@Composable
public fun AboutMe(profile: UserProfile, modifier: Modifier = Modifier, maxLines: Int = 2) {
    EpandableText(profile.aboutMe ?: "", modifier, maxLines)
}

@Preview
@Composable
private fun AboutMe() {
    AboutMe(
        UserProfile(
            "",
            aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
        ),
    )
}
