package com.gravatar.quickeditor.ui.avatarpicker

import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.repository.IdentityAvatars
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()
    private val avatarRepository = mockk<AvatarRepository>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(createAvatar("1"), createAvatar("2"))
    private val identityAvatars = IdentityAvatars(avatars, "1")

    @Before
    fun setup() {
        coEvery { profileService.retrieveCatching(email) } returns Result.Failure(ErrorType.UNKNOWN)
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(identityAvatars)
    }

    @Test
    fun `given view model initialization when avatars request succeed then uiState is updated`() = runTest {
        viewModel = initViewModel()

        viewModel.uiState.test {
            assertEquals(AvatarPickerUiState(email = email), awaitItem())
            assertEquals(
                AvatarPickerUiState(email = email, isLoading = true, profile = null),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = null,
                ),
                awaitItem(),
            )
            skipItems(2) // skipping profile loading states
        }
    }

    @Test
    fun `given view model initialization when avatars request fails then uiState is updated`() = runTest {
        coEvery { avatarRepository.getAvatars(email) } returns Result.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

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

        viewModel = initViewModel()

        viewModel.uiState.test {
            assertEquals(AvatarPickerUiState(email = email), awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loaded(profile),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `given view model initialization when fetch profile error then uiState is updated`() = runTest {
        viewModel = initViewModel()

        viewModel.uiState.test {
            assertEquals(AvatarPickerUiState(email = email), awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = null,
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected successful then uiState is updated`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.selectAvatar(avatars.first())
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = avatars.first().imageId,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars.copy(selectedAvatarId = avatars.first().imageId),
                    error = false,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelected(avatars.first()), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected failure then uiState is updated`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Failure(QuickEditorError.Server)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.selectAvatar(avatars.first())
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = avatars.first().imageId,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = false,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            expectNoEvents()
        }
    }

    private fun initViewModel() = AvatarPickerViewModel(email, profileService, avatarRepository)

    private fun createAvatar(id: String) = Avatar {
        imageUrl = "/image/url"
        format = 0
        imageId = id
        rating = "G"
        altText = "alt"
        isCropped = true
        updatedDate = Instant.now()
    }
}
