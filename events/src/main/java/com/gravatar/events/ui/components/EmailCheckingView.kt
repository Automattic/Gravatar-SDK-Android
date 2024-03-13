package com.gravatar.events.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gravatar.GravatarApi
import com.gravatar.models.UserProfiles
import com.gravatar.ui.components.ProfileListItem

@Composable
public fun EmailCheckingView(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        // TODO: check for invalid hash (not supported by gravatar)
        if (hash.isEmpty()) {
            Text("Please scan your own badge first", modifier = Modifier.padding(16.dp))
        } else {
            EmailChecking(hash, onEmailValidated, modifier)
        }
    }
}

@Composable
private fun EmailChecking(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val gravatarApi = GravatarApi()
    var emailValidated by remember { mutableStateOf(false) }
    var profiles by remember { mutableStateOf(UserProfiles(), neverEqualPolicy()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    AnimatedVisibility(visible = !emailValidated) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Enter the email address that matches your badge")
            Spacer(modifier = Modifier.height(8.dp))
            EmailCheck(
                hash,
                modifier = Modifier.fillMaxWidth(),
                onEmailValidated = {
                    emailValidated = it
                    onEmailValidated(it)
                    // Get profile
                    loading = true
                    gravatarApi.getProfile(
                        hash,
                        object : GravatarApi.GravatarListener<UserProfiles> {
                            override fun onSuccess(response: UserProfiles) {
                                profiles = response
                                loading = false
                            }

                            override fun onError(errorType: GravatarApi.ErrorType) {
                                // TODO: error management
                                error = errorType.name
                                loading = false
                            }
                        },
                    )
                },
            )
        }
    }
    AnimatedVisibility(visible = emailValidated) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                Toast.makeText(
                    LocalContext.current,
                    "Thank you for validating your email address!",
                    Toast.LENGTH_LONG,
                ).show()
                if (profiles.entry.isNotEmpty()) {
                    ProfileListItem(
                        profile = profiles.entry[0],
                        modifier = Modifier.padding(8.dp),
                        avatarImageSize = 56.dp,
                    )
                }
            }
        }
    }
}
