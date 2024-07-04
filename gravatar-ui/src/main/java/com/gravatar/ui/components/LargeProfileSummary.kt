package com.gravatar.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.UserInfo
import com.gravatar.ui.components.atomic.ViewProfileButton
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import java.net.URI
import com.gravatar.api.models.Profile as LegacyProfile

/**
 * [LargeProfileSummary] is a composable that displays a user's profile in a resumed way.
 * Given a [Profile], it displays a [LargeProfileSummary] using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun LargeProfileSummary(profile: Profile, modifier: Modifier = Modifier) {
    LargeProfileSummary(state = ComponentState.Loaded(profile), modifier = modifier)
}

/**
 * [LargeProfileSummary] is a composable that displays a user's profile in a resumed way.
 * Given a [ComponentState] for a [Profile], it displays a [LargeProfileSummary] in the appropriate state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param avatar Composable to display the user avatar
 * @param viewProfile Composable to display the view profile button
 */
@JvmName("LargeProfileWithComponentState")
@Composable
public fun LargeProfileSummary(
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
            inlineContent = null,
        )
    },
) {
    GravatarTheme {
        Surface {
            EmptyProfileClickableContainer(state) {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    avatar(state)
                    DisplayName(
                        state = state,
                        modifier = Modifier.padding(top = 16.dp),
                        skeletonModifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(top = 12.dp),
                    )
                    UserInfo(
                        state = state,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                        ),
                        skeletonModifier = Modifier.fillMaxWidth(0.8f),
                    )
                    viewProfile(state)
                }
            }
        }
    }
}

/**
 * [LargeProfileSummary] is a composable that displays a user's profile in a resumed way.
 * Given a [LegacyProfile], it displays a [LargeProfileSummary] using the atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.LargeProfileSummary"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun LargeProfileSummary(profile: LegacyProfile, modifier: Modifier = Modifier) {
    LargeProfileSummary(state = ComponentState.Loaded(profile), modifier = modifier)
}

/**
 * [LargeProfileSummary] is a composable that displays a user's profile in a resumed way.
 * Given a [ComponentState] for a [LegacyProfile], it displays a [LargeProfileSummary] in the appropriate state.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.LargeProfileSummary"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun LargeProfileSummary(state: ComponentState<LegacyProfile>, modifier: Modifier = Modifier) {
    LargeProfileSummary(state = state.toApi2ComponentStateProfile(), modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun LargeProfileSummaryPreview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        LargeProfileSummary(
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
                        url = URI("https://mastodon.social/@ddoe")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    VerifiedAccount {
                        serviceType = "tumblr"
                        serviceLabel = "Tumblr"
                        url = URI("https://ddoe.tumblr.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    VerifiedAccount {
                        serviceType = "wordpress"
                        serviceLabel = "WordPress"
                        url = URI("https://ddoe.wordpress.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                ),
                description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                    "doctor away. This about me description is quite long, this is good for testing.",
            ),
        )
    }
}

// TODO Make this preview internal in a future major release
@Deprecated(
    "This function is deprecated and will be removed in a future release.",
    level = DeprecationLevel.WARNING,
)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
public fun LargeProfileLoadingPreview() {
    LoadingToLoadedProfileStatePreview { LargeProfileSummary(it) }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileEmptyPreview() {
    GravatarTheme {
        Surface {
            LargeProfileSummary(ComponentState.Empty as ComponentState<Profile>)
        }
    }
}
