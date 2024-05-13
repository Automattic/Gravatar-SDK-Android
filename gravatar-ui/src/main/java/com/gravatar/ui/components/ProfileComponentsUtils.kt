package com.gravatar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import com.gravatar.GravatarConstants

@Composable
internal fun EmptyProfileClickableContainer(userProfileState: UserProfileState?, content: @Composable () -> Unit) {
    if (userProfileState is UserProfileState.Empty) {
        Box(Modifier.emptyProfileClick(userProfileState)) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun Modifier.emptyProfileClick(userProfileState: UserProfileState?): Modifier {
    return if (userProfileState is UserProfileState.Empty) {
        val uriHandler = LocalUriHandler.current
        this.clickable {
            uriHandler.openUri(GravatarConstants.GRAVATAR_SIGN_IN_URL)
        }
    } else {
        this
    }
}
