package com.gravatar.ui.components.atomic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.api.models.UserProfile

@Composable
public fun Location(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    dialogContent: @Composable (() -> Unit)? = null,
) {
    ExpandableText(profile.currentLocation.orEmpty(), modifier, maxLines, dialogContent)
}

@Preview
@Composable
private fun LocationPreview() {
    Location(UserProfile("", currentLocation = "Crac'h, France"))
}
