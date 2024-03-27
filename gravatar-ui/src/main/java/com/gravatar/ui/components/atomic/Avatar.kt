package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.AvatarQueryOptions
import com.gravatar.api.models.UserProfile
import com.gravatar.extensions.avatarUrl

@Composable
public fun Avatar(
    profile: UserProfile,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
) {
    val preferredSize = with(LocalDensity.current) { size.roundToPx() }
    AsyncImage(
        profile.avatarUrl(
            // Override the preferredSize
            avatarQueryOptions?.copy(
                preferredSize = preferredSize,
            ) ?: AvatarQueryOptions(preferredSize = preferredSize),
        ).url().toString(),
        contentDescription = "User profile image",
        modifier = modifier.size(size),
    )
}

@Preview
@Composable
private fun PreviewAvatar() {
    Avatar(UserProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"), 256.dp)
}
