package com.gravatar.quickeditor.ui.oauth

import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.types.Email
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class OAuthPageTest : RoborazziTest() {
    private val oAuthParams = OAuthParams {
        clientId = "client_id"
        clientSecret = "client_secret"
        redirectUri = "redirect_uri"
    }

    @Test
    fun oAuthPageLight() = gravatarScreenshotTest {
        OauthPage(
            uiState = OAuthUiState(),
            email = Email("email"),
            oAuthParams = oAuthParams,
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun oAuthPageDark() {
        gravatarScreenshotTest {
            OauthPage(
                uiState = OAuthUiState(),
                email = Email("email"),
                oAuthParams = oAuthParams,
            )
        }
    }
}
