package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.FileUtils
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.Instant

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()
    private val avatarRepository = mockk<AvatarRepository>()
    private val fileUtils = mockk<FileUtils>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(createAvatar("1"), createAvatar("2"))
    private val identityAvatars = IdentityAvatars(emptyList(), null)

    @Before
    fun setup() {
        coEvery { profileService.retrieveCatching(email) } returns Result.Failure(ErrorType.UNKNOWN)
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(identityAvatars)
    }

    @Test
    fun `given view model initialization when avatars request succeed then uiState is updated`() = runTest {
        val identityAvatarsCopy = identityAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(identityAvatarsCopy)

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
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = null,
                    scrollToIndex = 0,
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
                AvatarPickerUiState(email = email, error = SectionError.Unknown),
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
                    error = null,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = null,
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
                    error = null,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatars,
                    error = null,
                    profile = null,
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected successful then uiState is updated`() = runTest {
        val identityAvatarsCopy = identityAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(identityAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = avatars.last().imageId,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy.copy(selectedAvatarId = avatars.last().imageId),
                    error = null,
                    profile = ComponentState.Loaded(profile.copyAvatar(avatars.last())),
                    selectingAvatarId = null,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelected(avatars.last()), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected failure then uiState is updated`() = runTest {
        val identityAvatarsCopy = identityAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(identityAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = avatars.last().imageId,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelectionFailed, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when reselected then nothing happens`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.first()))
            expectNoEvents()
        }
        viewModel.actions.test {
            expectNoEvents()
        }
    }

    @Test
    fun `given local image when selected then launch cropper action sent`() = runTest {
        val file = mockk<File>()
        val uri = mockk<Uri>()
        every { fileUtils.createCroppedAvatarFile() } returns file

        viewModel = initViewModel()

        viewModel.actions.test {
            viewModel.onEvent(AvatarPickerEvent.LocalImageSelected(uri))
            assertEquals(AvatarPickerAction.LaunchImageCropper(uri, file), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload successful then uiState is updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = identityAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns Result.Success(Unit)
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(identityAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = uri,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
            skipItems(1) // extra state to fetch the avatars again
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = null,
                ),
                awaitItem(),
            )
        }
        verify { fileUtils.deleteFile(uri) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload failure then uiState is updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = identityAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns Result.Failure(QuickEditorError.Request(ErrorType.SERVER))
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(identityAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = uri,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    identityAvatars = identityAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = null,
                    scrollToIndex = 0,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarUploadFailed(uri), awaitItem())
        }
    }

    @Test
    fun `given view model when LoginUserTapped then LoginUser action sent`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(AvatarPickerEvent.LoginUserTapped)

        viewModel.actions.test {
            assertEquals(AvatarPickerAction.LoginUser, awaitItem())
        }
    }

    private fun initViewModel() = AvatarPickerViewModel(email, profileService, avatarRepository, fileUtils)

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
