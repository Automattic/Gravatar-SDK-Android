package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.AvatarQueryOptions
import com.gravatar.api.models.Profile
import com.gravatar.extensions.avatarUrl
import com.gravatar.extensions.emptyProfile
import com.gravatar.ui.R
import com.gravatar.ui.components.LoadingToLoadedStatePreview
import com.gravatar.ui.components.UserProfileState
import com.gravatar.ui.components.isNightModeEnabled
import com.gravatar.ui.skeletonEffect

/**
 * [Avatar] is a composable that displays a user's avatar.
 *
 * @param profile The user's profile information
 * @param size The size of the avatar
 * @param modifier Composable modifier
 * @param avatarQueryOptions Options to customize the avatar query
 */
@Composable
public fun Avatar(
    profile: Profile,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
) {
    val preferredSize = with(LocalDensity.current) { size.roundToPx() }
    Avatar(
        model = profile.avatarUrl(
            // Override the preferredSize
            avatarQueryOptions?.copy(
                preferredSize = preferredSize,
            ) ?: AvatarQueryOptions(preferredSize = preferredSize),
        ).url().toString(),
        size = size,
        modifier = modifier,
    )
}

@Composable
private fun Avatar(model: Any?, size: Dp, modifier: Modifier) {
    AsyncImage(
        model = model,
        contentDescription = "User profile image",
        modifier = modifier.size(size),
    )
}

/**
 * [Avatar] is a composable that displays a user's avatar.
 *
 * @param state
 * @param size The size of the avatar
 * @param modifier Composable modifier
 * @param avatarQueryOptions Options to customize the avatar query
 */
@Composable
public fun Avatar(
    state: UserProfileState,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
) {
    when (state) {
        is UserProfileState.Loading -> {
            Box(
                modifier = modifier
                    .size(size)
                    .skeletonEffect(),
            )
        }

        is UserProfileState.Loaded -> {
            Avatar(
                profile = state.userProfile,
                size = size,
                modifier = modifier,
                avatarQueryOptions = avatarQueryOptions,
            )
        }

        UserProfileState.Empty -> Avatar(
            model = if (isNightModeEnabled()) {
                R.drawable.empty_profile_avatar_dark
            } else {
                R.drawable.empty_profile_avatar
            },
            size = size,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun AvatarPreview() {
    Avatar(emptyProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"), 256.dp)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AvatarStatePreview() {
    LoadingToLoadedStatePreview { Avatar(it, 256.dp) }
}

@Preview
@Composable
private fun AvatarEmptyPreview() {
    Avatar(UserProfileState.Empty, 256.dp)
}
