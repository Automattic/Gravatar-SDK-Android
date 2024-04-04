package com.gravatar.ui.components.atomic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gravatar.api.models.UserProfile

@Composable
public fun DisplayName(profile: UserProfile, modifier: Modifier = Modifier) {
    Text(text = profile.displayName.orEmpty(), modifier = modifier)
}
