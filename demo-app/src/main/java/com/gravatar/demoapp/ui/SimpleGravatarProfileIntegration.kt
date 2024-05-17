package com.gravatar.demoapp.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ProfileSummary
import com.gravatar.ui.components.UserProfileState

@Composable
fun GravatarProfileSummary(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Set the default profile state to loading
    var profileState: UserProfileState by remember { mutableStateOf(UserProfileState.Loading, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        // Set the profile state to loading
        profileState = UserProfileState.Loading
        // Fetch the user profile
        when (val result = profileService.fetch(Email(emailAddress))) {
            is Result.Success -> {
                // Update the profile state with the loaded profile
                result.value.let {
                    profileState = UserProfileState.Loaded(it)
                }
            }
            is Result.Failure -> {
                // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
                // Here we log the error, but ideally we should show an error to the user.
                Log.e("Gravatar", result.error.name)
                // Set the Empty state on error
                profileState = UserProfileState.Empty
            }
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(profileState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}
