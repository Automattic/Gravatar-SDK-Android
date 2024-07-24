package com.gravatar.quickeditor.ui.oauth

import app.cash.turbine.test
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OAuthViewModelTest {
    @get:Rule
    var containerRule = CoroutineTestRule()

    private val wordPressOAuthService = mockk<WordPressOAuthService>()

    private lateinit var viewModel: OAuthViewModel

    @Before
    fun setup() {
        viewModel = OAuthViewModel(wordPressOAuthService)
    }

    @Test
    fun `given viewModel when initialized then OAuthAction_StartOAuth sent`() = runTest {
        viewModel.actions.test {
            assertEquals(OAuthAction.StartOAuth, awaitItem())
        }
    }

    @Test
    fun `given oAuth params when fetching the access token then UiState_IsAuthorizing is properly updated`() = runTest {
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Success("access_token")

        viewModel.uiState.test {
            assertEquals(OAuthUiState(isAuthorizing = false), awaitItem())

            viewModel.fetchAccessToken("code", "client_id", "client_secret", "redirect_uri")
            assertEquals(OAuthUiState(isAuthorizing = true), awaitItem())

            assertEquals(OAuthUiState(isAuthorizing = false), awaitItem())
        }
    }

    @Test
    fun `given oAuth params when fetching token fails then OAuthAction_AuthorizationFailure sent`() = runTest {
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Failure(ErrorType.UNKNOWN)

        viewModel.actions.test {
            skipItems(1) // skipping the StartOAuth action
            viewModel.fetchAccessToken("code", "client_id", "client_secret", "redirect_uri")
            assertEquals(OAuthAction.AuthorizationFailure, awaitItem())
        }
    }

    @Test
    fun `given oAuth params when fetching token successful then OAuthAction_AuthorizationSuccess sent`() = runTest {
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Success("access_token")

        viewModel.actions.test {
            skipItems(1) // skipping the StartOAuth action
            viewModel.fetchAccessToken("code", "client_id", "client_secret", "redirect_uri")
            assertEquals(OAuthAction.AuthorizationSuccess, awaitItem())
        }
    }
}