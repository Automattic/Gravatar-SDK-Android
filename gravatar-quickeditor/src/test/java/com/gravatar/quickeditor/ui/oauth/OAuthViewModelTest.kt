package com.gravatar.quickeditor.ui.oauth

import app.cash.turbine.test
import com.gravatar.quickeditor.data.storage.ProfileStorage
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Profile
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
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
    private val profileStorage = mockk<ProfileStorage>()

    private lateinit var viewModel: OAuthViewModel

    private val token = "access_token"
    private val email = Email("email")

    @Before
    fun setup() {
        coEvery { tokenStorage.storeToken(any(), any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(mockk())
        coEvery { profileStorage.getLoginIntroShown(any()) } returns false
        coEvery { profileStorage.setLoginIntroShown(any()) } returns Unit
        viewModel = OAuthViewModel(email, tokenStorage, profileStorage, profileService)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given oAuth params when token stored then email association checked sent`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns GravatarResult.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )
        advanceUntilIdle()

        coVerify { profileService.checkAssociatedEmailCatching(token, email) }
    }

    @Test
    fun `given token when email associated then OAuthAction_AuthorizationSuccess sent`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns GravatarResult.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        viewModel.actions.test {
            assertEquals(OAuthAction.AuthorizationSuccess, awaitItem())
        }
    }

    @Test
    fun `given wrong email when restarting OAuth flow after email error then UiState_Status keeps the error state`() =
        runTest {
            coEvery { profileService.checkAssociatedEmailCatching(any(), any()) } returns GravatarResult.Success(false)

            viewModel.uiState.test {
                assertEquals(OAuthStatus.LoginRequired, awaitItem().status)
                viewModel.tokenReceived(email, token)
                skipItems(1) // skipping the OAuthStatus.Authorizing state
                assertEquals(OAuthStatus.WrongEmailAuthorized, awaitItem().status)
                viewModel.startOAuth()
                expectNoEvents()
            }
        }

    @Test
    fun `given token when association check failed then UiState_Status updated`() = runTest {
        coEvery {
            profileService.checkAssociatedEmailCatching(token, email)
        } returns GravatarResult.Failure(ErrorType.Unknown())

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
        coEvery {
            profileService.checkAssociatedEmailCatching(token, email)
        } returns GravatarResult.Failure(ErrorType.Unknown())

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
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns GravatarResult.Success(true)

        viewModel.tokenReceived(
            email,
            token = token,
        )

        advanceUntilIdle()

        coEvery { tokenStorage.storeToken(email.hash().toString(), token) }
    }

    @Test
    fun `given token when email not associated then UiState updated`() = runTest {
        coEvery { profileService.checkAssociatedEmailCatching(token, email) } returns GravatarResult.Success(false)

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

    @Test
    fun `when fetchProfile succeeds then UiState is updated with profile`() = runTest {
        val profile = mockk<Profile>()
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)

        viewModel = createViewModel()

        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(ComponentState.Loading, awaitItem().profile)
            assertEquals(ComponentState.Loaded(profile), awaitItem().profile)
        }
    }

    @Test
    fun `when fetchProfile fails then UiState is updated with null profile`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Failure(ErrorType.Unknown())

        viewModel = createViewModel()

        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(ComponentState.Loading, awaitItem().profile)
            assertEquals(null, awaitItem().profile)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when init and loginIntroShown is true then StartOAuth action is sent`() = runTest {
        coEvery { profileStorage.getLoginIntroShown(email.hash().toString()) } returns true

        viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.actions.test {
            assertEquals(OAuthAction.StartOAuth, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when init and loginIntroShown is false then no action is sent`() = runTest {
        coEvery { profileStorage.getLoginIntroShown(email.hash().toString()) } returns false

        viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.actions.test {
            expectNoEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when startOAuth is called then loginIntroShown is set and StartOAuth action is sent`() = runTest {
        coEvery { profileStorage.setLoginIntroShown(email.hash().toString()) } returns Unit

        viewModel = createViewModel()
        viewModel.startOAuth()

        advanceUntilIdle()

        coVerify { profileStorage.setLoginIntroShown(email.hash().toString()) }
    }

    private fun createViewModel(): OAuthViewModel {
        return OAuthViewModel(email, tokenStorage, profileStorage, profileService)
    }
}
