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
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.UserProfileLoadingState.Loaded
import com.gravatar.ui.components.UserProfileLoadingState.Loading
import kotlinx.coroutines.delay

/**
 * [UserProfileLoadingState] represents the state of a user profile loading.
 * It can be in a [Loading] state or a [Loaded] state.
 */
public sealed class UserProfileLoadingState {
    /**
     * [Loading] represents the state where the user profile is still loading.
     */
    public data object Loading : UserProfileLoadingState()

    /**
     * [Loaded] represents the state where the user profile has been loaded.
     *
     * @property userProfile The user's profile information
     */
    public data class Loaded(val userProfile: UserProfile) : UserProfileLoadingState()
}

@Preview
@Composable
public fun LoadingToLoadedStatePreview(composable: @Composable (state: UserProfileLoadingState) -> Unit = {}) {
    var state: UserProfileLoadingState by remember { mutableStateOf(Loading) }
    LaunchedEffect(key1 = state) {
        delay(5000)
        state = Loaded(
            UserProfile(
                "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
                displayName = "John Doe",
                currentLocation = "Crac'h, France",
            ),
        )
    }
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            composable.invoke(state)
        }
    }
}
