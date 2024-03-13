package com.gravatar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.gravatar.DefaultAvatarImage
import com.gravatar.gravatarUrl
import com.gravatar.models.Email
import com.gravatar.models.UserProfile

@Composable
public fun ProfileListHeader(profile: UserProfile?, modifier: Modifier = Modifier, avatarImageSize: Dp = 96.dp) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (profile != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        gravatarUrl(
                            profile.hash ?: "",
                            size = with(LocalDensity.current) { avatarImageSize.toPx().toInt() },
                            defaultAvatarImage = DefaultAvatarImage.Monster,
                        ),
                    )
                    .build(),
                contentDescription = "User profile image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(avatarImageSize),
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(Modifier.align(Alignment.CenterVertically)) {
                (profile.displayName ?: profile.preferredUsername)?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                    )
                }

                profile.aboutMe?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Left,
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .height(avatarImageSize)
                    .fillMaxWidth()
                    .background(shimmerBrush()),
            )
        }
    }
}

@Preview
@Composable
private fun ProfileListItemPreview() {
    ProfileListHeader(
        UserProfile(
            displayName = "John Doe",
            preferredUsername = "johndoe",
            aboutMe = "I'm a farmer who loves to code",
            emails = arrayListOf(Email(primary = true, value = "john@doe.com")),
        ),
    )
}
