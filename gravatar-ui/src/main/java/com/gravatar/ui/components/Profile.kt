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
import com.gravatar.api.models.Profile
import com.gravatar.api.models.VerifiedAccount
import com.gravatar.extensions.emptyProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.AboutMe
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.SocialIconRow
import com.gravatar.ui.components.atomic.UserInfo
import com.gravatar.ui.components.atomic.ViewProfileButton
import java.net.URI

/**
 * [Profile] is a composable that displays a user's profile card.
 * Given a [Profile], iit displays a profile UI component using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun Profile(profile: Profile, modifier: Modifier = Modifier) {
    Profile(state = ComponentState.Loaded(profile), modifier = modifier)
}

/**
 * [Profile] is a composable that displays a user's profile card.
 * Given a [ComponentState] for a [Profile], it displays a [Profile] in the appropriate state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param avatar Composable to display the user avatar
 * @param viewProfile Composable to display the view profile button
 */
@Composable
public fun Profile(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    avatar: @Composable ((state: ComponentState<Profile>) -> Unit) = { profileState ->
        Avatar(
            state = profileState,
            size = 72.dp,
            modifier = Modifier.clip(CircleShape),
        )
    },
    viewProfile: @Composable ((state: ComponentState<Profile>) -> Unit) = { profileState ->
        ViewProfileButton(state, Modifier.padding(0.dp))
    },
) {
    GravatarTheme {
        Surface {
            EmptyProfileClickableContainer(state) {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        avatar(state)
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
                        viewProfile(state)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePreview() {
    Profile(
        emptyProfile(
            hash = "1234567890",
            displayName = "Dominique Doe",
            jobTitle = "Farmer",
            company = "Farmers United",
            location = "Crac'h, France",
            pronouns = "They/Them",
            verifiedAccounts = listOf(
                VerifiedAccount(
                    serviceType = "mastodon",
                    serviceLabel = "Mastodon",
                    url = URI("https://mastodon.social/@ddoe"),
                    serviceIcon = URI("https://example.com/icon.svg"),
                ),
                VerifiedAccount(
                    serviceType = "tumblr",
                    serviceLabel = "Tumblr",
                    url = URI("https://ddoe.tumblr.com"),
                    serviceIcon = URI("https://example.com/icon.svg"),
                ),
                VerifiedAccount(
                    serviceType = "wordpress",
                    serviceLabel = "WordPress",
                    url = URI("https://ddoe.wordpress.com"),
                    serviceIcon = URI("https://example.com/icon.svg"),
                ),
            ),
            description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
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
        Surface {
            Profile(ComponentState.Empty)
        }
    }
}
