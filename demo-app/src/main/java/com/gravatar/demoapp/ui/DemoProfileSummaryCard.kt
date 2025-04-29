package com.gravatar.demoapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.restapi.models.Profile
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.ProfileSummary
import com.gravatar.ui.components.atomic.Avatar

@Composable
fun DemoProfileSummaryCard(email: String, avatarCache: String? = null, modifier: Modifier = Modifier) {
    val profileService = ProfileService()

    var profileState: ComponentState<Profile> by remember { mutableStateOf(ComponentState.Loading, neverEqualPolicy()) }

    LaunchedEffect(email) {
        profileState = ComponentState.Loading
        when (val result = profileService.retrieveCatching(Email(email))) {
            is GravatarResult.Success -> {
                result.value.let {
                    profileState = ComponentState.Loaded(it)
                }
            }

            is GravatarResult.Failure -> {
                profileState = ComponentState.Empty
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        ProfileSummary(
            profileState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            avatar = {
                Avatar(
                    email = Email(email),
                    size = 72.dp,
                    cacheBuster = avatarCache,
                    modifier = Modifier.clip(CircleShape),
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GravatarProfileSummaryPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            DemoProfileSummaryCard(
                email = "test@gravatar.com",
            )
        }
    }
}
