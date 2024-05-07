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
import com.gravatar.api.models.Account
import com.gravatar.api.models.Email
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.UserProfileState.Loaded
import com.gravatar.ui.components.UserProfileState.Loading
import kotlinx.coroutines.delay

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
    public data class Loaded(val userProfile: UserProfile) : UserProfileState()

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
            UserProfile(
                hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
                displayName = "John Doe",
                preferredUsername = "ddoe",
                jobTitle = "Farmer",
                company = "Farmers United",
                currentLocation = "Crac'h, France",
                pronouns = "They/Them",
                accounts = listOf(
                    Account(name = "Mastodon", url = "https://example.com", shortname = "mastodon"),
                    Account(name = "Tumblr", url = "https://example.com", shortname = "tumblr"),
                    // Invalid url, should be ignored:
                    Account(name = "TikTok", url = "example.com", shortname = "tiktok"),
                    Account(name = "WordPress", url = "https://example.com", shortname = "wordpress"),
                    Account(name = "GitHub", url = "https://example.com", shortname = "github"),
                ),
                aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                    "doctor away. This about me description is quite long, this is good for testing.",
                emails = listOf(Email(primary = true, value = "john@doe.com")),
            ),
        )
    }
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            composable.invoke(state)
        }
    }
}
