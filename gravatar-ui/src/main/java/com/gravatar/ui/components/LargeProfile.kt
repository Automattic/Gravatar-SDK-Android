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
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.AboutMe
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.SocialIconRow
import com.gravatar.ui.components.atomic.UserInfo
import com.gravatar.ui.components.atomic.ViewProfileButton
import com.gravatar.ui.components.atomic.offsetGravatarIcon
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import java.net.URI
import com.gravatar.api.models.Profile as LegacyProfile

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
@JvmName("LargeProfileWithComponentState")
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
            state = profileState,
            modifier = Modifier.padding(0.dp),
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
                        state = state,
                        modifier = Modifier.padding(top = 16.dp),
                        skeletonModifier = Modifier
                            .fillMaxWidth(0.65f)
                            .padding(top = 12.dp),
                    )
                    UserInfo(
                        state = state,
                        skeletonModifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 4.dp),
                    )
                    AboutMe(
                        state = state,
                        modifier = Modifier.padding(top = 8.dp),
                        skeletonModifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 12.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SocialIconRow(
                            state = state,
                            modifier = Modifier.offsetGravatarIcon(),
                            maxIcons = 4,
                        )
                        viewProfile(state)
                    }
                }
            }
        }
    }
}

/**
 * [LargeProfile] is a composable that displays a user's profile card.
 * Given a [ComponentState], it displays a [LargeProfile] using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.LargeProfile"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun LargeProfile(profile: LegacyProfile, modifier: Modifier = Modifier) {
    LargeProfile(state = ComponentState.Loaded(profile.toApi2Profile()), modifier = modifier)
}

/**
 * [LargeProfile] is a composable that displays a user's profile card.
 * Given a [ComponentState] for a [Profile], it displays a [LargeProfile] or the skeleton if it's in a loading state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.LargeProfile"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun LargeProfile(state: ComponentState<LegacyProfile>, modifier: Modifier = Modifier) {
    LargeProfile(state = state.toApi2ComponentStateProfile(), modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun LargeProfilePreview() {
    LargeProfile(
        defaultProfile(
            hash = "1234567890",
            displayName = "Dominique Doe",
            jobTitle = "Farmer",
            company = "Farmers United",
            location = "Crac'h, France",
            pronouns = "They/Them",
            verifiedAccounts = listOf(
                VerifiedAccount {
                    serviceType = "mastodon"
                    serviceLabel = "Mastodon"
                    serviceIcon = URI("https://example.com")
                    url = URI("https://mastodon.social/@ddoe")
                },
                VerifiedAccount {
                    serviceType = "tumblr"
                    serviceLabel = "Tumblr"
                    serviceIcon = URI("https://example.com")
                    url = URI("https://ddoe.tumblr.com")
                },
                VerifiedAccount {
                    serviceType = "wordpress"
                    serviceLabel = "WordPress"
                    serviceIcon = URI("https://example.com")
                    url = URI("https://ddoe.wordpress.com")
                },
            ),
            description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
        ),
    )
}

// TODO Make this preview internal in a future major release
@Deprecated(
    "This function is deprecated and will be removed in a future release.",
    level = DeprecationLevel.WARNING,
)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
public fun DisplayNamePreview() {
    LoadingToLoadedProfileStatePreview { LargeProfile(it) }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileEmptyPreview() {
    GravatarTheme {
        Surface {
            LargeProfile(ComponentState.Empty as ComponentState<Profile>)
        }
    }
}
