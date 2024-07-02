package com.gravatar.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * [LargeProfile] is a composable that displays a user's profile card.
 * Given a [ComponentState], it displays a [LargeProfile] using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun LargeProfile(profile: Profile, modifier: Modifier = Modifier) {
    LargeProfile(state = ComponentState.Loaded(profile), modifier = modifier)
}

/**
 * [LargeProfile] is a composable that displays a user's profile card.
 * Given a [ComponentState] for a [Profile], it displays a [LargeProfile] or the skeleton if it's in a loading state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param avatar Composable to display the user avatar
 * @param viewProfile Composable to display the view profile button
 */
@Composable
public fun LargeProfile(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    avatar: @Composable ((state: ComponentState<Profile>) -> Unit) = { profileState ->
        Avatar(
            state = profileState,
            size = 132.dp,
            modifier = Modifier.clip(CircleShape),
        )
    },
    viewProfile: @Composable ((state: ComponentState<Profile>) -> Unit) = { profileState ->
        ViewProfileButton(
            profileState,
            Modifier.padding(0.dp),
        )
    },
) {
    GravatarTheme {
        Surface {
            EmptyProfileClickableContainer(state) {
                Column(
                    modifier = modifier,
                ) {
                    avatar(state)
                    DisplayName(
                        state,
                        modifier = Modifier.padding(top = 16.dp),
                        skeletonModifier = Modifier.fillMaxWidth(0.65f).padding(top = 12.dp),
                    )
                    UserInfo(
                        state,
                        skeletonModifier = Modifier.fillMaxWidth(0.9f).padding(top = 4.dp),
                    )
                    AboutMe(
                        state,
                        modifier = Modifier.padding(top = 8.dp),
                        skeletonModifier = Modifier.fillMaxWidth(0.9f).padding(top = 12.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
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

@Preview(showBackground = true)
@Composable
private fun LargeProfilePreview() {
    LargeProfile(
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
                    serviceIcon = URI("https://example.com"),
                    url = URI("https://mastodon.social/@ddoe"),
                ),
                VerifiedAccount(
                    serviceType = "tumblr",
                    serviceLabel = "Tumblr",
                    serviceIcon = URI("https://example.com"),
                    url = URI("https://ddoe.tumblr.com"),
                ),
                VerifiedAccount(
                    serviceType = "wordpress",
                    serviceLabel = "WordPress",
                    serviceIcon = URI("https://example.com"),
                    url = URI("https://ddoe.wordpress.com"),
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
public fun DisplayNamePreview() {
    LoadingToLoadedStatePreview { LargeProfile(it) }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileEmptyPreview() {
    GravatarTheme {
        Surface {
            LargeProfile(ComponentState.Empty)
        }
    }
}
