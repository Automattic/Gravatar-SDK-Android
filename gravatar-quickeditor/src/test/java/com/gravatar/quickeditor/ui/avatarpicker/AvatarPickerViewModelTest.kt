package com.gravatar.quickeditor.ui.avatarpicker

import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val tokenStorage = mockk<TokenStorage>()
    private val avatarService = mockk<AvatarService>()
    private val profileService = mockk<ProfileService>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val token = "token"
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(mockk<Avatar>())

    @Before
    fun setup() {
        coEvery { profileService.retrieveCatching(email) } returns Result.Failure(ErrorType.UNKNOWN)
        coEvery { tokenStorage.getToken(email.hash().toString()) } returns token
        coEvery { avatarService.retrieveCatching(token) } returns Result.Success(avatars)
    }

    @Test
    fun `given view model initialization when token present and avatars request succeed then uiState is updated`() =
        runTest {
            viewModel = AvatarPickerViewModel(email, avatarService, profileService, tokenStorage)

            viewModel.uiState.test {
                assertEquals(AvatarPickerUiState(email = email), awaitItem())
                assertEquals(
                    AvatarPickerUiState(email = email, isLoading = true, profile = null),
                    awaitItem(),
                )
                assertEquals(
                    AvatarPickerUiState(email = email, avatars = avatars, error = false, profile = null),
                    awaitItem(),
                )
                skipItems(2) // skipping profile loading states
            }
        }

    @Test
    fun `given view model initialization when token present and avatars request fails then uiState is updated`() =
        runTest {
            coEvery { avatarService.retrieveCatching(token) } returns Result.Failure(ErrorType.UNKNOWN)

            viewModel = AvatarPickerViewModel(email, avatarService, profileService, tokenStorage)

            viewModel.uiState.test {
                assertEquals(AvatarPickerUiState(email = email), awaitItem())
                assertEquals(
                    AvatarPickerUiState(email = email, isLoading = true),
                    awaitItem(),
                )
                assertEquals(
                    AvatarPickerUiState(email = email, error = true),
                    awaitItem(),
                )
                skipItems(2) // skipping profile loading states
            }
        }

    @Test
    fun `given view model initialization when fetch profile successful then uiState is updated`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)

        viewModel = AvatarPickerViewModel(email, avatarService, profileService, tokenStorage)

        viewModel.uiState.test {
            assertEquals(AvatarPickerUiState(email = email), awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    avatars = avatars,
                    error = false,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    avatars = avatars,
                    error = false,
                    profile = ComponentState.Loaded(profile),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `given view model initialization when fetch profile error then uiState is updated`() = runTest {
        viewModel = AvatarPickerViewModel(email, avatarService, profileService, tokenStorage)

        viewModel.uiState.test {
            assertEquals(AvatarPickerUiState(email = email), awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    avatars = avatars,
                    error = false,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    avatars = avatars,
                    error = false,
                    profile = null,
                ),
                awaitItem(),
            )
        }
    }
}
