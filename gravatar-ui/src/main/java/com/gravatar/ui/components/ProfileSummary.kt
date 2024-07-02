package com.gravatar.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.Location
import com.gravatar.ui.components.atomic.ViewProfileButton
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import com.gravatar.api.models.Profile as LegacyProfile

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [Profile], it displays a profile summary card using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun ProfileSummary(profile: Profile, modifier: Modifier = Modifier) {
    ProfileSummary(state = ComponentState.Loaded(profile), modifier = modifier)
}

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [ComponentState] for a [Profile], it displays a profile summary card using the other atomic components.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param avatar Composable to display the user avatar
 * @param viewProfile Composable to display the view profile button
 */
@JvmName("ProfileSummaryWithComponentState")
@Composable
public fun ProfileSummary(
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
        ViewProfileButton(
            state = profileState,
            modifier = Modifier.height(32.dp),
        )
    },
) {
    GravatarTheme {
        Surface {
            EmptyProfileClickableContainer(state) {
                Row(modifier = modifier) {
                    avatar(state)
                    Column(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .align(Alignment.CenterVertically),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        DisplayName(
                            state = state,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            skeletonModifier = Modifier.fillMaxWidth(0.5f),
                        )
                        Location(
                            state = state,
                            skeletonModifier = Modifier.fillMaxWidth(0.9f),
                        )
                        viewProfile(state)
                    }
                }
            }
        }
    }
}

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [Profile], it displays a profile summary card using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.ProfileSummary"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun ProfileSummary(profile: LegacyProfile, modifier: Modifier = Modifier) {
    ProfileSummary(state = ComponentState.Loaded(profile.toApi2Profile()), modifier = modifier)
}

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [ComponentState] for a [Profile], it displays a profile summary card using the other atomic components.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.ProfileSummary"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun ProfileSummary(state: ComponentState<LegacyProfile>, modifier: Modifier = Modifier) {
    ProfileSummary(state = state.toApi2ComponentStateProfile(), modifier = modifier)
}

@Preview
@Composable
private fun ProfileSummaryPreview() {
    ProfileSummary(
        defaultProfile(
            hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
            displayName = "John Doe",
            location = "Crac'h, France",
        ),
    )
}

@Preview
@Composable
private fun ProfileSummaryWithoutLocationPreview() {
    ProfileSummary(
        defaultProfile(
            hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
            displayName = "John Doe",
        ),
    )
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfileSummaryLoadingPreview() {
    LoadingToLoadedProfileStatePreview { ProfileSummary(it) }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfileSummaryEmptyPreview() {
    GravatarTheme {
        Surface {
            ProfileSummary(ComponentState.Empty as ComponentState<Profile>)
        }
    }
}
