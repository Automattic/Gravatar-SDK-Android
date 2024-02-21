package com.gravatar.demoapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.gravatarUrl
import com.gravatar.models.Email
import com.gravatar.models.UserProfile

@Composable
fun ProfileCard(profile: UserProfile, avatarImageSize: Dp = 128.dp) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            gravatarUrl(profile.hash ?: "", size = with(LocalDensity.current) { avatarImageSize.toPx().toInt() }),
            contentDescription = "User profile image",
            modifier = Modifier.clip(CircleShape).size(avatarImageSize),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.displayName ?: profile.preferredUsername ?: "",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        profile.preferredUsername?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = profile.aboutMe ?: "",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = profile.emails.firstOrNull()?.value ?: "",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreviewUserProfileCard() {
    ProfileCard(
        UserProfile(
            displayName = "John Doe",
            preferredUsername = "johndoe",
            aboutMe = "I'm a farmer who loves to code",
            emails = arrayListOf(Email(primary = "john@doe.com")),
        ),
    )
}
