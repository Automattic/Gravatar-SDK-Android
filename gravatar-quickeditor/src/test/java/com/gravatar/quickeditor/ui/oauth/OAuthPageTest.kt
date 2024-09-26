package com.gravatar.quickeditor.ui.oauth

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.types.Email
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class OAuthPageTest : RoborazziTest() {
    @Test
    fun oAuthPageLight() = gravatarScreenshotTest {
        OauthPage(
            uiState = OAuthUiState(),
            email = Email("email"),
            onStartOAuthClicked = {},
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun oAuthPageDark() {
        gravatarScreenshotTest {
            OauthPage(
                uiState = OAuthUiState(),
                email = Email("email"),
                onStartOAuthClicked = {},
            )
        }
    }
}
