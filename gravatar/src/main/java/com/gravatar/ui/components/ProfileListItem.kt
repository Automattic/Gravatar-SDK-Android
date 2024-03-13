package com.gravatar.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
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
public fun ProfileListItem(profile: UserProfile?, modifier: Modifier = Modifier, avatarImageSize: Dp = 128.dp) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (profile != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        gravatarUrl(
                            profile?.hash ?: "",
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

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.padding(start = 8.dp)) {
                if (profile != null) {
                    (profile.displayName ?: profile.preferredUsername)?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Left,
                        )
                    }

                    profile.aboutMe?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            textAlign = TextAlign.Left,
                        )
                    }
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

public fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
public fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "",
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero,
        )
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
