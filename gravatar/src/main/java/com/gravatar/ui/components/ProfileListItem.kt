package com.gravatar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.DefaultAvatarImage
import com.gravatar.gravatarUrl
import com.gravatar.models.Email
import com.gravatar.models.UserProfile

@Composable
public fun ProfileListItem(profile: UserProfile, modifier: Modifier = Modifier, avatarImageSize: Dp = 128.dp) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            gravatarUrl(
                profile.hash ?: "",
                size = with(LocalDensity.current) { avatarImageSize.toPx().toInt() },
                defaultAvatarImage = DefaultAvatarImage.Monster,
            ),
            contentDescription = "User profile image",
            modifier = Modifier
                .clip(CircleShape)
                .size(avatarImageSize),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(Modifier.padding(start = 8.dp)) {
            (profile.displayName ?: profile.preferredUsername)?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }

            profile.aboutMe?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileListItemPreview() {
    ProfileListItem(
        UserProfile(
            displayName = "John Doe",
            preferredUsername = "johndoe",
            aboutMe = "I'm a farmer who loves to code",
            emails = arrayListOf(Email(primary = true, value = "john@doe.com")),
        ),
    )
}
