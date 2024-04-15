package com.gravatar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.Location
import com.gravatar.ui.components.atomic.ViewProfileButton

/**
 * [MiniProfileCard] is a composable that displays a mini profile card.
 * Given a [UserProfile], it displays a mini profile card using the other atomic components provided within the SDK.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 */
@Composable
public fun MiniProfileCard(profile: UserProfile, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Avatar(
            profile = profile,
            size = 72.dp,
            modifier = Modifier.clip(CircleShape),
        )
        Column(modifier = Modifier.padding(start = 14.dp)) {
            ProvideTextStyle(TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)) {
                DisplayName(profile)
            }
            if (!profile.currentLocation.isNullOrBlank()) {
                ProvideTextStyle(
                    TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    Location(profile)
                }
            }
            ProvideTextStyle(
                MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.onBackground),
            ) {
                ViewProfileButton(
                    profile,
                    modifier = Modifier.height(32.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun MiniProfileCardPreview() {
    MiniProfileCard(
        UserProfile(
            "1234",
            displayName = "John Doe",
            currentLocation = "Crac'h, France",
        ),
    )
}
