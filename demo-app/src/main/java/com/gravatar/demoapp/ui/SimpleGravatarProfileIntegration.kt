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
import com.gravatar.types.Email
import com.gravatar.ui.components.MiniProfileCard
import com.gravatar.ui.components.UserProfileState

@Composable
fun SimpleGravatarProfileIntegration(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Create a mutable state for the user profile state
    var profileState: UserProfileState? by remember { mutableStateOf(null, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        try {
            // Fetch the user profile
            profileState = UserProfileState.Loading
            profileState = profileService.fetch(Email(emailAddress)).entry.firstOrNull()?.let {
                UserProfileState.Loaded(it)
            }
        } catch (exception: ProfileService.FetchException) {
            // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
            // Here we log the error, but ideally we should show an error to the user.
            Log.e("Gravatar", exception.errorType.name)
        }
    }

    // Show the profile as a ProfileCard
    profileState?.let {
        MiniProfileCard(it, modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}
