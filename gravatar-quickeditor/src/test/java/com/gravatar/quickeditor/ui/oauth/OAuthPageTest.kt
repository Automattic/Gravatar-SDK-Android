package com.gravatar.quickeditor.ui.oauth

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class OAuthPageTest : RoborazziTest() {
    @Test
    fun oAuthPageLight() = gravatarScreenshotTest {
        OauthPage(
            uiState = OAuthUiState(
                profile = ComponentState.Empty,
            ),
            email = Email("email"),
            onStartOAuthClicked = {},
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun oAuthPageDark() {
        gravatarScreenshotTest {
            OauthPage(
                uiState = OAuthUiState(
                    profile = ComponentState.Empty,
                ),
                email = Email("email"),
                onStartOAuthClicked = {},
            )
        }
    }

    @Test
    fun oAuthPageAuthorizing() = gravatarScreenshotTest {
        OauthPage(
            uiState = OAuthUiState(
                profile = ComponentState.Empty,
                status = OAuthStatus.Authorizing,
            ),
            email = Email("email"),
            onStartOAuthClicked = {},
        )
    }
}
