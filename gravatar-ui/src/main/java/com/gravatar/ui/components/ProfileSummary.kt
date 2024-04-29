package com.gravatar.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.Location
import com.gravatar.ui.components.atomic.ViewProfileButton

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [UserProfile], it displays a mini profile card using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun ProfileSummary(profile: UserProfile, modifier: Modifier = Modifier) {
    ProfileSummary(state = UserProfileState.Loaded(profile), modifier = modifier)
}

/**
 * [ProfileSummary] is a composable that displays a mini profile card.
 * Given a [UserProfile], it displays a mini profile card using the other atomic components provided within the SDK.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 */
@Composable
public fun ProfileSummary(state: UserProfileState, modifier: Modifier = Modifier) {
    GravatarTheme {
        Row(modifier = modifier) {
            Avatar(
                state = state,
                size = 72.dp,
                modifier = Modifier.clip(CircleShape),
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                DisplayName(
                    state,
                    textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                when (state) {
                    is UserProfileState.Loaded -> {
                        if (!state.userProfile.currentLocation.isNullOrBlank()) {
                            Location(state)
                        }
                    }

                    UserProfileState.Loading -> {
                        Location(state, modifier.width(120.dp))
                    }
                }
                ViewProfileButton(
                    state,
                    modifier = Modifier.height(32.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileSummaryPreview() {
    ProfileSummary(
        UserProfile(
            "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
            displayName = "John Doe",
            currentLocation = "Crac'h, France",
        ),
    )
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfileSummaryLoadingPreview() {
    LoadingToLoadedStatePreview { ProfileSummary(it) }
}
