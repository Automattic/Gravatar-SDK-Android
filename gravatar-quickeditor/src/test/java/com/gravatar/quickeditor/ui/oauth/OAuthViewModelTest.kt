package com.gravatar.quickeditor.ui.oauth

import app.cash.turbine.test
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

    private val tokenStorage = mockk<TokenStorage>()
    private val profileService = mockk<ProfileService>()

    private lateinit var viewModel: OAuthViewModel

    val token = "access_token"
    val email = Email("email")

    @Before
    fun setup() {
        coEvery { tokenStorage.storeToken(any(), any()) } returns Unit
        viewModel = OAuthViewModel(tokenStorage, profileService)
    }

    @Test
    fun `given viewModel when initialized then OAuthAction_StartOAuth sent`() = runTest {
        viewModel.actions.test {
            assertEquals(OAuthAction.StartOAuth, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given oAuth params when token stored then email association checked sent`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )
        advanceUntilIdle()

        coVerify { profileService.checkAssociatedEmailCatching(token, email) }
    }

    @Test
    fun `given token when email associated then OAuthAction_AuthorizationSuccess sent`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        viewModel.actions.test {
            expectMostRecentItem()
            assertEquals(OAuthAction.AuthorizationSuccess, awaitItem())
        }
    }

    @Test
    fun `given wrong email when restarting OAuth flow after email error then UiState_Status keeps the error state`() =
        runTest {
            coEvery { profileService.checkAssociatedEmailCatching(any(), any()) } returns Result.Success(false)

            viewModel.uiState.test {
                assertEquals(OAuthUiState(OAuthStatus.LoginRequired), awaitItem())
                viewModel.tokenReceived(email, token)
                skipItems(1) // skipping the OAuthStatus.Authorizing state
                assertEquals(OAuthUiState(OAuthStatus.WrongEmailAuthorized), awaitItem())
                viewModel.startOAuth()
                expectNoEvents()
            }
        }

    @Test
    fun `given token when association check failed then UiState_Status updated`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Failure(ErrorType.Unknown)

        viewModel.uiState.test {
            expectMostRecentItem()

            viewModel.tokenReceived(email, token = token)

            assertEquals(OAuthStatus.Authorizing, awaitItem().status)
            assertEquals(OAuthStatus.EmailAssociatedCheckError(token), awaitItem().status)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given token when association check failed then token stored`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Failure(ErrorType.Unknown)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        advanceUntilIdle()

        coEvery { tokenStorage.storeToken(email.hash().toString(), token) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given token when email associated then token stored sent`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        advanceUntilIdle()

        coEvery { tokenStorage.storeToken(email.hash().toString(), token) }
    }

    @Test
    fun `given token when email not associated then UiState updated`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns Result.Success(false)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(OAuthStatus.Authorizing, awaitItem().status)
            assertEquals(OAuthStatus.WrongEmailAuthorized, awaitItem().status)
        }

        coEvery { tokenStorage.storeToken(email.hash().toString(), token) }
    }
}
