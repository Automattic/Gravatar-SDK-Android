package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.repository.EmailAvatars
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import io.mockk.coEvery
import io.mockk.coVerify
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

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()
    private val avatarRepository = mockk<AvatarRepository>()
    private val fileUtils = mockk<FileUtils>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val contentLayout = ContentLayout.Horizontal
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(createAvatar("1"), createAvatar("2"))
    private val emailAvatars = EmailAvatars(emptyList(), null)

    @Before
    fun setup() {
        coEvery { profileService.retrieveCatching(email) } returns Result.Failure(ErrorType.UNKNOWN)
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(emailAvatars)
    }

    @Test
    fun `given view model initialization when avatars request succeed then uiState is updated`() = runTest {
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState = AvatarPickerUiState(email = email, contentLayout = contentLayout)
            assertEquals(avatarPickerUiState, awaitItem())
            assertEquals(
                avatarPickerUiState.copy(isLoading = true, profile = null),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emailAvatarsCopy,
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
            val avatarPickerUiState = AvatarPickerUiState(email = email, contentLayout = contentLayout)
            assertEquals(avatarPickerUiState, awaitItem())
            assertEquals(
                avatarPickerUiState.copy(isLoading = true),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(error = SectionError.Unknown),
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
            val avatarPickerUiState = AvatarPickerUiState(email = email, contentLayout = contentLayout)
            assertEquals(avatarPickerUiState, awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emailAvatars,
                    error = null,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emailAvatars,
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
            val avatarPickerUiState = AvatarPickerUiState(email = email, contentLayout = contentLayout)
            assertEquals(avatarPickerUiState, awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emailAvatars,
                    error = null,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emailAvatars,
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
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(emailAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = emailAvatarsCopy,
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = avatars.last().imageId,
                scrollToIndex = 0,
                contentLayout = contentLayout,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    emailAvatars = emailAvatarsCopy.copy(selectedAvatarId = avatars.last().imageId),
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
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns Result.Success(emailAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns Result.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = emailAvatarsCopy,
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = avatars.last().imageId,
                scrollToIndex = 0,
                contentLayout = contentLayout,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    selectingAvatarId = null,
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
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        val uploadedAvatar = createAvatar("3")
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns Result.Success(uploadedAvatar)
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = emailAvatarsCopy,
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uri,
                scrollToIndex = 0,
                contentLayout = contentLayout,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    emailAvatars = emailAvatarsCopy.copy(
                        avatars = buildList {
                            add(uploadedAvatar)
                            addAll(emailAvatarsCopy.avatars)
                        },
                    ),
                    uploadingAvatar = null,
                    scrollToIndex = null,
                ),
                awaitItem(),
            )
        }
        verify { fileUtils.deleteFile(uri) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar returned when avatar with the same id then uiState is updated`() = runTest {
        val uri = mockk<Uri>()
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        val uploadedAvatar = createAvatar("2")
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns Result.Success(uploadedAvatar)
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = emailAvatarsCopy,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = uri,
                    scrollToIndex = 0,
                    contentLayout = ContentLayout.Horizontal,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = emailAvatarsCopy.copy(
                        avatars = buildList {
                            add(uploadedAvatar)
                            add(createAvatar("1"))
                        },
                    ),
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = null,
                    scrollToIndex = null,
                    contentLayout = ContentLayout.Horizontal,
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
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns Result.Failure(QuickEditorError.Request(ErrorType.SERVER))
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = emailAvatarsCopy,
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uri,
                scrollToIndex = 0,
                contentLayout = contentLayout,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    uploadingAvatar = null,
                    scrollToIndex = null,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarUploadFailed(uri), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload successful then scrollToIndex updated`() = runTest {
        val uri = mockk<Uri>()
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "2")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns Result.Success(createAvatar("3"))
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(1, awaitItem().scrollToIndex) // initial scroll to after loading avatars

            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(0, awaitItem().scrollToIndex) // set to 0 to show the loading item
            assertEquals(
                null,
                awaitItem().scrollToIndex,
            ) // set to null, if we leave it as 0 the scroll won't work with the next upload

            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(0, awaitItem().scrollToIndex)
            assertEquals(null, awaitItem().scrollToIndex)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload failed then scrollToIndex updated`() = runTest {
        val uri = mockk<Uri>()
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "2")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns Result.Failure(QuickEditorError.Request(ErrorType.SERVER))
        coEvery { avatarRepository.getAvatars(any()) } returns Result.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(1, awaitItem().scrollToIndex) // initial scroll to after loading avatars

            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(0, awaitItem().scrollToIndex) // set to 0 to show the loading item
            assertEquals(
                null,
                awaitItem().scrollToIndex,
            ) // set to null, if we leave it as 0 the scroll won't work with the next upload

            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            assertEquals(0, awaitItem().scrollToIndex)
            assertEquals(null, awaitItem().scrollToIndex)
        }
    }

    @Test
    fun `given view model when LoginUserTapped then LoginUser action sent`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(AvatarPickerEvent.HandleAuthFailureTapped)

        viewModel.actions.test {
            assertEquals(AvatarPickerAction.InvokeAuthFailed, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given profile loaded when refresh then fetch profile not called`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns Result.Success(profile)
        coEvery { avatarRepository.getAvatars(email) } returns Result.Failure(QuickEditorError.Unknown)
        viewModel = initViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 1) { profileService.retrieveCatching(email) }
    }

    private fun initViewModel(handleExpiredSession: Boolean = true) =
        AvatarPickerViewModel(email, handleExpiredSession, contentLayout, profileService, avatarRepository, fileUtils)

    private fun createAvatar(id: String) = Avatar {
        imageUrl = "/image/url"
        imageId = id
        rating = Avatar.Rating.G
        altText = "alt"
        updatedDate = ""
    }
}
