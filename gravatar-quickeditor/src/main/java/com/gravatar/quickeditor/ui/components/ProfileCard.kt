package com.gravatar.quickeditor.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.AvatarQueryOptions
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.ProfileSummary
import com.gravatar.ui.components.atomic.Avatar

@Composable
internal fun ProfileCard(profile: ComponentState<Profile>?, modifier: Modifier = Modifier) {
    GravatarCard(modifier) { backgroundColor ->
        profile?.let {
            ProfileSummary(
                state = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                avatar = {
                    val size = 72.dp
                    val sizePx = with(LocalDensity.current) { size.roundToPx() }
                    Avatar(
                        state = profile.transform { profileValue -> profileValue.avatarUrl },
                        size = size,
                        modifier = Modifier.clip(CircleShape),
                        avatarQueryOptions = AvatarQueryOptions { preferredSize = sizePx },
                    )
                },
            )
        }
    }
}

@Composable
internal fun GravatarCard(modifier: Modifier = Modifier, content: @Composable (Color) -> Unit) {
    val cardModifier = modifier
        .wrapContentHeight()
    val shape = RoundedCornerShape(8.dp)
    if (isSystemInDarkTheme()) {
        Card(
            modifier = cardModifier,
            shape = shape,
        ) {
            content(MaterialTheme.colorScheme.surfaceContainerHigh)
        }
    } else {
        ElevatedCard(
            modifier = cardModifier,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            shape = shape,
        ) {
            content(MaterialTheme.colorScheme.surface)
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF000000, heightDp = 200)
@Composable
private fun ProfileCardPreview() {
    GravatarTheme {
        ProfileCard(
            profile = ComponentState.Loaded(
                defaultProfile(hash = "dfadf", "John Travolta"),
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

private fun <T, O> ComponentState<T>.transform(transform: (T) -> O): ComponentState<O> = when (this) {
    is ComponentState.Empty -> ComponentState.Empty
    is ComponentState.Loading -> ComponentState.Loading
    is ComponentState.Loaded -> ComponentState.Loaded(transform(loadedValue))
}
