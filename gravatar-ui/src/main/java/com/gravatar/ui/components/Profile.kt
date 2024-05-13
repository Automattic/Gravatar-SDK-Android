package com.gravatar.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.api.models.Account
import com.gravatar.api.models.Email
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.AboutMe
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.SocialIconRow
import com.gravatar.ui.components.atomic.UserInfo
import com.gravatar.ui.components.atomic.ViewProfileButton

/**
 * [Profile] is a composable that displays a user's profile card.
 * Given a [UserProfile], it displays a [Profile] using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun Profile(profile: UserProfile, modifier: Modifier = Modifier) {
    Profile(state = UserProfileState.Loaded(profile), modifier = modifier)
}

/**
 * [Profile] is a composable that displays a user's profile card.
 * Given a [UserProfileState], it displays a [Profile] or the skeleton if it's in a loading state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 */
@Composable
public fun Profile(state: UserProfileState, modifier: Modifier = Modifier) {
    GravatarTheme {
        EmptyProfileClickableContainer(state) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(
                        state = state,
                        size = 72.dp,
                        modifier = Modifier.clip(CircleShape),
                    )
                    Column(modifier = Modifier.padding(14.dp, 0.dp, 0.dp, 0.dp)) {
                        DisplayName(
                            state,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        UserInfo(state)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                AboutMe(state)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SocialIconRow(state, maxIcons = 4)
                    ViewProfileButton(state, Modifier.padding(0.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePreview() {
    Profile(
        UserProfile(
            hash = "1234567890",
            displayName = "Dominique Doe",
            preferredUsername = "ddoe",
            jobTitle = "Farmer",
            company = "Farmers United",
            currentLocation = "Crac'h, France",
            pronouns = "They/Them",
            accounts = listOf(
                Account(name = "Mastodon", url = "https://mastodon.social/@ddoe"),
                Account(name = "Tumblr", url = "https://ddoe.tumblr.com"),
                Account(name = "WordPress", url = "https://ddoe.wordpress.com"),
            ),
            aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
            emails = listOf(Email(primary = true, value = "john@doe.com")),
        ),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileLoadingPreview() {
    LoadingToLoadedStatePreview { Profile(it) }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileEmptyPreview() {
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            Profile(UserProfileState.Empty)
        }
    }
}
