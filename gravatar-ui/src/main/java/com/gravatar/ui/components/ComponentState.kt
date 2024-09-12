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
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState.Loaded
import com.gravatar.ui.components.ComponentState.Loading
import kotlinx.coroutines.delay
import java.net.URI

/**
 * [ComponentState] represents the state of a user profile loading.
 * It can be in a [Loading] state or a [Loaded] state.
 */
public sealed class ComponentState<out T> {
    /**
     * [Loading] represents the state where the data is still loading.
     */
    public data object Loading : ComponentState<Nothing>()

    /**
     * [Loaded] represents the state where the user data has been loaded.
     *
     * @param T the type of the information to load
     * @property loadedValue The loaded information
     */
    public data class Loaded<T>(val loadedValue: T) : ComponentState<T>()

    /**
     * [Empty] represents the state where the data is empty
     */
    public data object Empty : ComponentState<Nothing>()
}

@Preview
@Composable
internal fun LoadingToLoadedProfileStatePreview(composable: @Composable (state: ComponentState<Profile>) -> Unit = {}) {
    var state: ComponentState<Profile> by remember { mutableStateOf(Loading) }
    LaunchedEffect(key1 = state) {
        delay(5000)
        state = Loaded(
            Profile {
                hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"
                displayName = "John Doe"
                jobTitle = "Farmer"
                company = "Farmers United"
                location = "Crac'h, France"
                pronouns = "They/Them"
                verifiedAccounts = listOf(
                    VerifiedAccount {
                        serviceType = "mastodon"
                        serviceLabel = "Mastodon"
                        url = URI("https://example.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    VerifiedAccount {
                        serviceType = "tumblr"
                        serviceLabel = "Tumblr"
                        url = URI("https://example.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    // Invalid url, should be ignored:
                    VerifiedAccount {
                        serviceType = "tiktok"
                        serviceLabel = "TikTok"
                        url = URI("https://example.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    VerifiedAccount {
                        serviceType = "wordpress"
                        serviceLabel = "WordPress"
                        url = URI("https://example.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                    VerifiedAccount {
                        serviceType = "github"
                        serviceLabel = "GitHub"
                        url = URI("https://example.com")
                        serviceIcon = URI("https://example.com/icon.svg")
                    },
                )
                description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                    "doctor away. This about me description is quite long, this is good for testing."
            },
        )
    }
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            composable.invoke(state)
        }
    }
}
