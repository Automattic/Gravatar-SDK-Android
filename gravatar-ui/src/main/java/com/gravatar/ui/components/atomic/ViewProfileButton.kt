package com.gravatar.ui.components.atomic

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import com.gravatar.api.models.UserProfile

@Composable
public fun ViewProfileButton(profile: UserProfile, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    TextButton(
        onClick = {
            uriHandler.openUri(profile.profileUrl.toString())
        },
        modifier = modifier,
    ) {
        Text("View Profile →")
    }
}
