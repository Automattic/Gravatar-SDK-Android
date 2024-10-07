package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.repository.EmailAvatars
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Error
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
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
import java.net.URI

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()
    private val avatarRepository = mockk<AvatarRepository>()
    private val fileUtils = mockk<FileUtils>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(createAvatar("1"), createAvatar("2"))
    private val emailAvatars = EmailAvatars(emptyList(), null)
    private val errorMessage = "errorMessage"
    private val invalidRequest = QuickEditorError.Request(
        ErrorType.InvalidRequest(
            Error {
                code = "code"
                error = errorMessage
            },
        ),
    )

    @Before
    fun setup() {
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Failure(ErrorType.Unknown)
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Success(emailAvatars)
    }

    @Test
    fun `given view model initialization when avatars request succeed then uiState is updated`() = runTest {
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Success(emailAvatarsCopy)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState =
                AvatarPickerUiState(email = email, avatarPickerContentLayout = avatarPickerContentLayout)
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
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState =
                AvatarPickerUiState(email = email, avatarPickerContentLayout = avatarPickerContentLayout)
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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState =
                AvatarPickerUiState(email = email, avatarPickerContentLayout = avatarPickerContentLayout)
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
            val avatarPickerUiState =
                AvatarPickerUiState(email = email, avatarPickerContentLayout = avatarPickerContentLayout)
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
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Success(emailAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)

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
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarUpdates = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    emailAvatars = emailAvatarsCopy.copy(selectedAvatarId = avatars.last().imageId),
                    selectingAvatarId = null,
                    scrollToIndex = 0,
                    avatarUpdates = 1,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelected, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected failure then uiState is updated`() = runTest {
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Success(emailAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Failure(QuickEditorError.Unknown)

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
                avatarPickerContentLayout = avatarPickerContentLayout,
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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)

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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        val uploadedAvatar = createAvatar("3")
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Success(uploadedAvatar)
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)

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
                avatarPickerContentLayout = avatarPickerContentLayout,
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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        val uploadedAvatar = createAvatar("2")
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Success(uploadedAvatar)
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)

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
                    avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                    failedUploads = emptySet(),
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
                    avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)

        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)

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
                avatarPickerContentLayout = avatarPickerContentLayout,
                failedUploads = emptySet(),
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    uploadingAvatar = null,
                    scrollToIndex = null,
                    failedUploads = setOf(AvatarUploadFailure(uri, errorMessage)),
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload successful then scrollToIndex updated`() = runTest {
        val uri = mockk<Uri>()
        val emailAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "2")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Success(createAvatar("3"))
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)

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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)

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
    fun `given multiple failed uploads when upload successful then uiState is updated`() = runTest {
        val uriOne = mockk<Uri>()
        val uriTwo = mockk<Uri>()
        val emailAvatarsCopy = emailAvatars.copy(avatars = emptyList(), selectedAvatarId = null)
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(emailAvatarsCopy)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uriOne))
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uriTwo))

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            coEvery {
                avatarRepository.uploadAvatar(any(), any())
            } returns GravatarResult.Success(createAvatar("1"))

            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uriTwo))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = emailAvatarsCopy,
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uriTwo,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                failedUploads = setOf(
                    AvatarUploadFailure(uriOne, errorMessage),
                ),
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    emailAvatars = emailAvatarsCopy.copy(avatars = listOf(createAvatar("1"))),
                    uploadingAvatar = null,
                    scrollToIndex = null,
                ),
                awaitItem(),
            )
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
    fun `given failed avatar upload when FailedAvatarTapped then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(identityAvatarsCopy)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

            assertEquals(AvatarUploadFailure(uri, errorMessage), awaitItem().failedUploadDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given failed avatar upload dialog shown when FailedAvatarDismissed then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(identityAvatarsCopy)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))
        viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.FailedAvatarDismissed(uri))

            val awaitItem = awaitItem()
            assertEquals(null, awaitItem.failedUploadDialog)
            assertEquals(emptySet<AvatarUploadFailure>(), awaitItem.failedUploads)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given failed avatar upload dialog shown when FailedAvatarDialogDismissed then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(identityAvatarsCopy)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))
        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.FailedAvatarDialogDismissed)

            val awaitItem = awaitItem()
            assertEquals(null, awaitItem.failedUploadDialog)
            assertEquals(setOf(AvatarUploadFailure(uri, errorMessage)), awaitItem.failedUploads)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given failed avatar upload dialog shown when ImageCropped then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val identityAvatarsCopy = emailAvatars.copy(avatars = avatars, selectedAvatarId = "1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.getAvatars(any()) } returns GravatarResult.Success(identityAvatarsCopy)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))
        viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            val awaitItem = awaitItem()
            assertEquals(null, awaitItem.failedUploadDialog)
            assertEquals(uri, awaitItem.uploadingAvatar)

            skipItems(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given profile loaded when refresh then fetch profile not called`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.getAvatars(email) } returns GravatarResult.Failure(QuickEditorError.Unknown)
        viewModel = initViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 1) { profileService.retrieveCatching(email) }
    }

    private fun initViewModel(handleExpiredSession: Boolean = true) = AvatarPickerViewModel(
        email,
        handleExpiredSession,
        avatarPickerContentLayout,
        profileService,
        avatarRepository,
        fileUtils,
    )

    private fun createAvatar(id: String) = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test")
        imageId = id
        rating = Avatar.Rating.G
        altText = "alt"
        updatedDate = ""
    }
}
