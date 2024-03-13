package com.gravatar.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import com.gravatar.utils.getDisplayName
import com.gravatar.utils.getPrimaryEmail

@Composable
public fun ProfileCard(profile: UserProfile, modifier: Modifier = Modifier, avatarImageSize: Dp = 128.dp) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            gravatarUrl(profile.hash ?: "", size = with(LocalDensity.current) { avatarImageSize.toPx().toInt() }),
            contentDescription = "User profile image",
            modifier = Modifier
                .clip(CircleShape)
                .size(avatarImageSize),
        )

        Spacer(modifier = Modifier.height(16.dp))

        profile.getDisplayName()?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        profile.preferredUsername?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        profile.aboutMe?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val emailLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { }

        // Find primary email
        val primaryEmail = profile.getPrimaryEmail()

        primaryEmail?.let {
            Button(
                onClick = {
                    emailLauncher.launch(
                        Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:") // Only email apps handle this.
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
                        },
                    )
                },
                modifier = Modifier.padding(8.dp),
            ) {
                Text(text = it)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewUserProfileCard() {
    ProfileCard(
        UserProfile(
            displayName = "John Doe",
            preferredUsername = "johndoe",
            aboutMe = "I'm a farmer who loves to code",
            emails = arrayListOf(Email(primary = true, value = "john@doe.com")),
        ),
    )
}