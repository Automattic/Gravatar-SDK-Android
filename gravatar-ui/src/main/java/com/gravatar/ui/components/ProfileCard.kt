package com.gravatar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.api.models.Account
import com.gravatar.api.models.Email
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.components.atomic.AboutMe
import com.gravatar.ui.components.atomic.Avatar
import com.gravatar.ui.components.atomic.DisplayName
import com.gravatar.ui.components.atomic.SocialIconRow
import com.gravatar.ui.components.atomic.UserInfo
import com.gravatar.ui.components.atomic.ViewProfileButton

@Composable
fun ProfileCard(profile: UserProfile, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(modifier = Modifier.padding(0.dp)) {
            Avatar(
                profile = profile,
                size = 72.dp,
                modifier = Modifier.clip(CircleShape),
            )
            Column(modifier = Modifier.padding(14.dp, 0.dp, 0.dp, 0.dp)) {
                ProvideTextStyle(TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)) {
                    DisplayName(profile)
                }
                ProvideTextStyle(
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    UserInfo(profile)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProvideTextStyle(MaterialTheme.typography.bodyMedium.merge(textAlign = TextAlign.Left)) {
            AboutMe(profile)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SocialIconRow(profile, maxIcons = 4)
            ProvideTextStyle(
                MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.onBackground),
            ) {
                ViewProfileButton(profile, Modifier.padding(0.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewUserProfileCard() {
    ProfileCard(
        UserProfile(
            hash = "1234567890",
            displayName = "Dominique Doe",
            preferredUsername = "ddoe",
            currentLocation = "Crac'h, France",
            pronouns = "They/Them",
            accounts = listOf(
                Account(name = "Mastodon", url = "https://mastodon.social/@ddoe"),
                Account(name = "Tumblr", url = "https://ddoe.tumblr.com"),
                Account(name = "WordPress", url = "https://ddoe.wordpress.com"),
            ),
            aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
            emails = arrayListOf(Email(primary = true, value = "john@doe.com")),
        ),
    )
}
