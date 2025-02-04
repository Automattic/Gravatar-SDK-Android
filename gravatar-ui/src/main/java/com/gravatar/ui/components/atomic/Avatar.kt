package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.extensions.avatarUrl
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.components.isNightModeEnabled
import com.gravatar.ui.skeletonEffect

/**
 * [Avatar] is a composable that displays a user's avatar.
 *
 * @param profile The user's profile information
 * @param size The size of the avatar
 * @param modifier Composable modifier
 * @param avatarQueryOptions Options to customize the avatar query
 * @param forceRefresh While this is true, we'll force the refresh of the avatar in every recomposition.
 *              When false, we'll use the default URL for that profile (without cache buster) to fetch the avatar.
 */
@Composable
public fun Avatar(
    profile: Profile,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
    forceRefresh: Boolean = false,
) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val cacheBuster = if (forceRefresh) {
        System.currentTimeMillis().toString()
    } else {
        null
    }

    Avatar(
        model = profile.avatarUrl(
            // Override the preferredSize
            AvatarQueryOptions {
                preferredSize = sizePx
                rating = avatarQueryOptions?.rating
                forceDefaultAvatar = avatarQueryOptions?.forceDefaultAvatar
                defaultAvatarOption = avatarQueryOptions?.defaultAvatarOption
            },
        ).url(cacheBuster).toString(),
        size = size,
        modifier = modifier,
    )
}

/**
 * [Avatar] is a composable that displays a user's avatar.
 *
 * @param state
 * @param size The size of the avatar
 * @param modifier Composable modifier
 * @param avatarQueryOptions Options to customize the avatar query
 * @param forceRefresh While this is true, we'll force the refresh of the avatar in every recomposition
 */
@Composable
public fun Avatar(
    state: ComponentState<Profile>,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
    forceRefresh: Boolean = false,
) {
    when (state) {
        is ComponentState.Loading -> SkeletonAvatar(size = size, modifier = modifier)

        is ComponentState.Loaded -> {
            Avatar(
                profile = state.loadedValue,
                size = size,
                modifier = modifier,
                avatarQueryOptions = avatarQueryOptions,
                forceRefresh = forceRefresh,
            )
        }

        ComponentState.Empty -> EmptyAvatar(size = size, modifier = modifier)
    }
}

/**
 * [Avatar] is a composable that displays a user's avatar.
 *
 * @param state The state of the avatar, when loaded it should contain the Avatar URL
 * @param size The size of the avatar
 * @param modifier Composable modifier
 */
@Composable
public fun Avatar(state: ComponentState<String>, size: Dp, modifier: Modifier = Modifier) {
    when (state) {
        is ComponentState.Loading -> SkeletonAvatar(size = size, modifier = modifier)

        is ComponentState.Loaded -> {
            Avatar(
                model = state.loadedValue,
                size = size,
                modifier = modifier,
            )
        }

        ComponentState.Empty -> EmptyAvatar(size = size, modifier = modifier)
    }
}

@Composable
private fun SkeletonAvatar(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .skeletonEffect(),
    )
}

@Composable
private fun EmptyAvatar(size: Dp, modifier: Modifier = Modifier) {
    Avatar(
        model = if (isNightModeEnabled()) {
            R.drawable.gravatar_empty_profile_avatar_dark
        } else {
            R.drawable.gravatar_empty_profile_avatar
        },
        size = size,
        modifier = modifier,
    )
}

@Composable
private fun Avatar(model: Any?, size: Dp, modifier: Modifier) {
    AsyncImage(
        model = model,
        contentDescription = stringResource(R.string.gravatar_ui_avatar_content_description),
        modifier = modifier.size(size),
    )
}

private enum class AvatarState {
    None,
    Loading,
    Loaded,
    Placeholder,
}

/**
 * Atomic Avatar composable that displays a user's avatar that is generated from the user's email address.
 * A skeleton overlay will be shown while loading the image.
 *
 * @param email The user's email address
 * @param size The size of the avatar
 * @param modifier Composable modifier
 * @param avatarQueryOptions Options to customize the avatar query
 * @param cacheBuster Random string value to force a cache bust
 */
@Composable
public fun Avatar(
    email: Email,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
    cacheBuster: String? = null,
) {
    var state by remember { mutableStateOf(AvatarState.None) }
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    Box(
        modifier = modifier.size(size),
    ) {
        AsyncImage(
            model = AvatarUrl(
                hash = email.hash(),
                avatarQueryOptions = AvatarQueryOptions {
                    preferredSize = sizePx
                    rating = avatarQueryOptions?.rating
                    forceDefaultAvatar = avatarQueryOptions?.forceDefaultAvatar
                    defaultAvatarOption = avatarQueryOptions?.defaultAvatarOption
                },
            ).url(cacheBuster).toString(),
            contentDescription = stringResource(R.string.gravatar_ui_avatar_content_description),
            onLoading = {
                state = AvatarState.Loading
            },
            onError = {
                state = AvatarState.Placeholder
            },
            onSuccess = {
                state = AvatarState.Loaded
            },
        )
        when (state) {
            AvatarState.Loading -> SkeletonAvatar(size = size)
            AvatarState.Placeholder -> EmptyAvatar(size = size)
            else -> Unit
        }
    }
}

@Preview
@Composable
private fun AvatarPreview() {
    Avatar(defaultProfile(hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"), 256.dp)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AvatarStatePreview() {
    LoadingToLoadedProfileStatePreview { Avatar(it, 256.dp) }
}

@Preview
@Composable
private fun AvatarEmptyPreview() {
    Avatar(ComponentState.Empty as ComponentState<Profile>, 256.dp)
}
