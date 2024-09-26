package com.gravatar.quickeditor.ui.oauth

import app.cash.turbine.test
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.services.ErrorType
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OAuthViewModelTest {
    @get:Rule
    var containerRule = CoroutineTestRule()

    private val wordPressOAuthService = mockk<WordPressOAuthService>()
    private val tokenStorage = mockk<TokenStorage>()
    private val profileService = mockk<ProfileService>()

    private lateinit var viewModel: OAuthViewModel

    @Before
    fun setup() {
        coEvery { tokenStorage.storeToken(any(), any()) } returns Unit
        viewModel = OAuthViewModel(wordPressOAuthService, tokenStorage, profileService)
    }

    @Test
    fun `given viewModel when initialized then OAuthAction_StartOAuth sent`() = runTest {
        viewModel.actions.test {
            assertEquals(OAuthAction.StartOAuth, awaitItem())
        }
    }

    @Test
    fun `given oAuth params when fetching the access token then UiState_Status is properly updated`() = runTest {
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Success("access_token")

        coEvery { profileService.checkAssociatedEmailCatching(any(), any()) } returns Result.Success(true)

        viewModel.uiState.test {
            assertEquals(OAuthUiState(OAuthStatus.LoginRequired), awaitItem())
            viewModel.fetchAccessToken(
                "code",
                OAuthParams {
                    clientId = "client_id"
                    clientSecret = "client_secret"
                    redirectUri = "redirect_uri"
                },
                Email("email"),
            )
            assertEquals(OAuthUiState(OAuthStatus.Authorizing), awaitItem())
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
        } returns Result.Failure(ErrorType.Unknown)

        viewModel.actions.test {
            skipItems(1) // skipping the StartOAuth action
            viewModel.fetchAccessToken(
                "code",
                OAuthParams {
                    clientId = "client_id"
                    clientSecret = "client_secret"
                    redirectUri = "redirect_uri"
                },
                Email("email"),
            )
            assertEquals(OAuthAction.AuthorizationFailure, awaitItem())
        }
    }

    @Test
    fun `given oAuth params when fetching token but email doesn't match then UiState_Status is properly updated`() =
        runTest {
            coEvery {
                wordPressOAuthService.getAccessToken(
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns Result.Success("access_token")

            coEvery { profileService.checkAssociatedEmailCatching(any(), any()) } returns Result.Success(false)

            viewModel.uiState.test {
                assertEquals(OAuthUiState(OAuthStatus.LoginRequired), awaitItem())
                viewModel.fetchAccessToken(
                    "code",
                    OAuthParams {
                        clientId = "client_id"
                        clientSecret = "client_secret"
                        redirectUri = "redirect_uri"
                    },
                    Email("email"),
                )
                skipItems(1) // skipping the OAuthStatus.Authorizing state
                assertEquals(OAuthUiState(OAuthStatus.WrongEmailAuthorized), awaitItem())
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

        coEvery { profileService.checkAssociatedEmailCatching(any(), any()) } returns Result.Success(true)

        viewModel.actions.test {
            skipItems(1) // skipping the StartOAuth action
            viewModel.fetchAccessToken(
                "code",
                OAuthParams {
                    clientId = "client_id"
                    clientSecret = "client_secret"
                    redirectUri = "redirect_uri"
                },
                Email("email"),
            )
            assertEquals(OAuthAction.AuthorizationSuccess, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given oAuth params when account for fetched token matches email then token saved`() = runTest {
        val token = "access_token"
        val email = Email("email")
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Success(token)

        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(true)

        viewModel.fetchAccessToken(
            "code",
            OAuthParams {
                clientId = "client_id"
                clientSecret = "client_secret"
                redirectUri = "redirect_uri"
            },
            email,
        )
        advanceUntilIdle()

        coVerify(exactly = 1) { tokenStorage.storeToken(email.hash().toString(), token) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given oAuth params when account for fetched token doesn't matches email then token is not saved`() = runTest {
        val token = "access_token"
        val email = Email("email")
        coEvery {
            wordPressOAuthService.getAccessToken(
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.Success(token)

        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(false)

        viewModel.fetchAccessToken(
            "code",
            OAuthParams {
                clientId = "client_id"
                clientSecret = "client_secret"
                redirectUri = "redirect_uri"
            },
            email,
        )
        advanceUntilIdle()

        coVerify(exactly = 0) { tokenStorage.storeToken(any(), token) }
    }
}
