package com.gravatar.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.Profile
import com.gravatar.api.models.VerifiedAccount
import com.gravatar.extensions.emptyProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.UserProfileState.Loaded
import com.gravatar.ui.components.UserProfileState.Loading
import kotlinx.coroutines.delay
import java.net.URI

/**
 * [UserProfileState] represents the state of a user profile loading.
 * It can be in a [Loading] state or a [Loaded] state.
 */
public sealed class UserProfileState {
    /**
     * [Loading] represents the state where the user profile is still loading.
     */
    public data object Loading : UserProfileState()

    /**
     * [Loaded] represents the state where the user profile has been loaded.
     *
     * @property userProfile The user's profile information
     */
    public data class Loaded(val userProfile: Profile) : UserProfileState()

    /**
     * [Empty] represents the state where the user profile is empty, so it can be claimed.
     */
    public data object Empty : UserProfileState()
}

@Preview
@Composable
public fun LoadingToLoadedStatePreview(composable: @Composable (state: UserProfileState) -> Unit = {}) {
    var state: UserProfileState by remember { mutableStateOf(Loading) }
    LaunchedEffect(key1 = state) {
        delay(5000)
        state = Loaded(
            emptyProfile(
                hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
                displayName = "John Doe",
                jobTitle = "Farmer",
                company = "Farmers United",
                location = "Crac'h, France",
                pronouns = "They/Them",
                verifiedAccounts = listOf(
                    VerifiedAccount(
                        serviceLabel = "Mastodon",
                        url = URI("https://example.com"),
                        serviceIcon = URI("https://example.com/icon.svg"),
                    ),
                    VerifiedAccount(
                        serviceLabel = "Tumblr",
                        url = URI("https://example.com"),
                        serviceIcon = URI("https://example.com/icon.svg"),
                    ),
                    // Invalid url, should be ignored:
                    VerifiedAccount(
                        serviceLabel = "TikTok",
                        url = URI("https://example.com"),
                        serviceIcon = URI("https://example.com/icon.svg"),
                    ),
                    VerifiedAccount(
                        serviceLabel = "WordPress",
                        url = URI("https://example.com"),
                        serviceIcon = URI("https://example.com/icon.svg"),
                    ),
                    VerifiedAccount(
                        serviceLabel = "GitHub",
                        url = URI("https://example.com"),
                        serviceIcon = URI("https://example.com/icon.svg"),
                    ),
                ),
                description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                    "doctor away. This about me description is quite long, this is good for testing.",
            ),
        )
    }
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            composable.invoke(state)
        }
    }
}
