package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.createAvatar
import com.gravatar.quickeditor.data.DownloadManagerError
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.ImageDownloader
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.time.Clock
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@Suppress("LargeClass")
class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()
    private val avatarRepository = mockk<AvatarRepository>()
    private val fileUtils = mockk<FileUtils>()
    private val imageDownloader = mockk<ImageDownloader>()
    private val clock = mockk<Clock>()
    private val avatarsFlow = MutableSharedFlow<List<Avatar>>(replay = 1)

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")
    private val avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")
    private val avatars = listOf(createAvatar("1"), createAvatar("2"))
    private val emptyEmailAvatars = emptyList<Avatar>().toEmailAvatars()
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
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Failure(ErrorType.Unknown())
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(emptyList())
        coEvery { avatarRepository.getAvatars(email) } returns avatarsFlow
        coEvery { clock.getTimeMillis() } returns 0
    }

    @Test
    fun `given view model initialization when avatars request succeed then uiState is updated`() = runTest {
        val avatars = listOf(createAvatar(id = "1", isSelected = true), createAvatar("2"))
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
            )
            assertEquals(avatarPickerUiState, awaitItem())
            assertEquals(
                avatarPickerUiState.copy(isLoading = true, profile = null),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = avatars.toEmailAvatars(),
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
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
            )
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
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
            )
            assertEquals(avatarPickerUiState, awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emptyEmailAvatars,
                    error = null,
                    profile = ComponentState.Loading,
                    nonSelectedAvatarAlertVisible = false,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emptyEmailAvatars,
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    nonSelectedAvatarAlertVisible = false,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `given view model initialization when fetch profile error then uiState is updated`() = runTest {
        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
            )
            assertEquals(avatarPickerUiState, awaitItem())
            skipItems(2) // skipping loading avatars states
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emptyEmailAvatars,
                    error = null,
                    profile = ComponentState.Loading,
                    nonSelectedAvatarAlertVisible = false,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    emailAvatars = emptyEmailAvatars,
                    error = null,
                    profile = null,
                    nonSelectedAvatarAlertVisible = false,
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when selected successful then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId("1")
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            coEvery { clock.getTimeMillis() } returns 1
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = avatars.last().imageId,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    selectingAvatarId = null,
                    avatarCacheBuster = 1,
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
        val avatars = avatars.selectAvatarId("1")
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarSelected(avatars.last()))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = avatars.last().imageId,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
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
        val avatars = avatars.selectAvatarId("1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        val uploadedAvatar = createAvatar("3")
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Success(uploadedAvatar)
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uri,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 0,
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
        verify { fileUtils.deleteFile(uri) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload failure then uiState is updated`() = runTest {
        val uri = mockk<Uri>()
        val avatars = avatars.selectAvatarId("1")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)

        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uri,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                failedUploads = emptySet(),
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    uploadingAvatar = null,
                    scrollToIndex = null,
                    failedUploads = setOf(AvatarUploadFailure(uri, invalidRequest.type)),
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given cropped image when upload successful then scrollToIndex updated`() = runTest {
        val uri = mockk<Uri>()
        val avatars = avatars.selectAvatarId("2")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Success(createAvatar("3"))
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

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
        val avatars = avatars.selectAvatarId("2")
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

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
        val avatars = listOf(createAvatar("3", isSelected = true))
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)
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
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uriTwo,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                failedUploads = setOf(
                    AvatarUploadFailure(uriOne, invalidRequest.type),
                ),
                avatarCacheBuster = 0,
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
        val avatars = avatars.selectAvatarId("1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

            assertEquals(AvatarUploadFailure(uri, invalidRequest.type), awaitItem().failedUploadDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given failed avatar upload dialog shown when FailedAvatarDismissed then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val avatars = avatars.selectAvatarId("1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

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
        val avatars = avatars.selectAvatarId("1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Failure(invalidRequest)
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

        viewModel = initViewModel()
        viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))
        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.FailedAvatarTapped(uri))

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.FailedAvatarDialogDismissed)

            val awaitItem = awaitItem()
            assertEquals(null, awaitItem.failedUploadDialog)
            assertEquals(setOf(AvatarUploadFailure(uri, invalidRequest.type)), awaitItem.failedUploads)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given failed avatar upload dialog shown when ImageCropped then UiState updated`() = runTest {
        val uri = mockk<Uri>()
        val avatars = avatars.selectAvatarId("1")
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.uploadAvatar(any(), any())
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)

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
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Failure(QuickEditorError.Unknown)
        viewModel = initViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 1) { profileService.retrieveCatching(email) }
    }

    @Suppress("LongMethod")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given no avatar selected when avatar upload success then avatar is selected - uiState is updated`() = runTest {
        val uriOne = mockk<Uri>()
        val avatars = listOf(createAvatar("3", isSelected = false))
        every { fileUtils.deleteFile(any()) } returns Unit
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)
        val uploadedAvatar = createAvatar(id = "1", isSelected = true)
        coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Success(uploadedAvatar)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            coEvery { clock.getTimeMillis() } returns 1
            viewModel.onEvent(AvatarPickerEvent.ImageCropped(uriOne))

            // State before upload starts
            var avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = uriOne,
                scrollToIndex = 0,
                avatarPickerContentLayout = avatarPickerContentLayout,
                nonSelectedAvatarAlertVisible = true,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            // State produced before finishing uploadAvatar
            // Note that emailAvatars will be updated though the observer when the repository emits the new value
            avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                selectingAvatarId = null,
                uploadingAvatar = null,
                scrollToIndex = null,
                avatarPickerContentLayout = avatarPickerContentLayout,
                avatarCacheBuster = 1,
                nonSelectedAvatarAlertVisible = true,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelected, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given selected avatar when delete successful then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId(avatars.first().imageId)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.deleteAvatar(any(), any()) } returns GravatarResult.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            val avatarToDelete = avatars.first()
            coEvery { clock.getTimeMillis() } returns 1
            viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatarToDelete.imageId))
            var avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.minus(avatarToDelete).toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                avatarPickerContentLayout = avatarPickerContentLayout,
                scrollToIndex = 0,
                nonSelectedAvatarAlertVisible = false,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )

            avatarPickerUiState = avatarPickerUiState.copy(
                avatarCacheBuster = 1,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            avatarPickerUiState = avatarPickerUiState.copy(
                nonSelectedAvatarAlertVisible = true,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarSelected, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given non selected avatar when delete successful then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId(avatars.first().imageId)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.deleteAvatar(any(), any()) } returns GravatarResult.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            val avatarToDelete = avatars.last() // Non selected avatar
            viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatarToDelete.imageId))
            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.minus(avatarToDelete).toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                avatarPickerContentLayout = avatarPickerContentLayout,
                scrollToIndex = 0,
                nonSelectedAvatarAlertVisible = false,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
        }
        viewModel.actions.test {
            expectNoEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given selected avatar when delete fails then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId(avatars.first().imageId)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.deleteAvatar(any(), any()) } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            val avatarToDelete = this@AvatarPickerViewModelTest.avatars.first()
            viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatarToDelete.imageId))

            awaitItem()

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                avatarPickerContentLayout = avatarPickerContentLayout,
                scrollToIndex = 0,
                nonSelectedAvatarAlertVisible = false,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            viewModel.actions.test {
                assertEquals(AvatarPickerAction.AvatarDeletionFailed(avatarToDelete.imageId), awaitItem())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given non selected avatar when delete fails then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId(avatars.first().imageId)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.deleteAvatar(any(), any()) } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            val avatarToDelete = this@AvatarPickerViewModelTest.avatars.last() // Non selected avatar
            viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatarToDelete.imageId))

            awaitItem()

            val avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                avatarPickerContentLayout = avatarPickerContentLayout,
                scrollToIndex = 0,
                nonSelectedAvatarAlertVisible = false,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            viewModel.actions.test {
                assertEquals(AvatarPickerAction.AvatarDeletionFailed(avatarToDelete.imageId), awaitItem())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Suppress("LongMethod")
    fun `given alert banner dismissed when avatar upload successful then nonSelectedAvatarAlertVisible is hidden`() =
        runTest {
            val uri = mockk<Uri>()
            every { fileUtils.deleteFile(any()) } returns Unit
            coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
            coEvery { avatarRepository.refreshAvatars(any()) } returns GravatarResult.Success(avatars)
            val uploadedAvatar = createAvatar("3", isSelected = true)
            coEvery { avatarRepository.uploadAvatar(any(), any()) } returns GravatarResult.Success(uploadedAvatar)

            viewModel = initViewModel()

            advanceUntilIdle()

            viewModel.uiState.test {
                coEvery { clock.getTimeMillis() } returns 1
                viewModel.onEvent(AvatarPickerEvent.AvatarDeleteAlertDismissed)
                expectMostRecentItem()
                viewModel.onEvent(AvatarPickerEvent.ImageCropped(uri))

                val avatarPickerUiState = AvatarPickerUiState(
                    email = email,
                    emailAvatars = avatars.toEmailAvatars(),
                    error = null,
                    profile = ComponentState.Loaded(profile),
                    selectingAvatarId = null,
                    uploadingAvatar = uri,
                    scrollToIndex = 0,
                    avatarPickerContentLayout = avatarPickerContentLayout,
                    nonSelectedAvatarAlertVisible = false,
                    avatarCacheBuster = 0,
                )
                assertEquals(
                    avatarPickerUiState,
                    awaitItem(),
                )
                assertEquals(
                    avatarPickerUiState.copy(
                        uploadingAvatar = null,
                        scrollToIndex = null,
                        avatarCacheBuster = 1,
                    ),
                    awaitItem(),
                )
                // Avatars are updated by the avatars flow in the repository
                val updatedAvatars = buildList {
                    add(uploadedAvatar)
                    addAll(avatars)
                }
                avatarsFlow.emit(updatedAvatars)
                assertEquals(
                    avatarPickerUiState.copy(
                        emailAvatars = updatedAvatars.toEmailAvatars(),
                        uploadingAvatar = null,
                        scrollToIndex = null,
                        avatarCacheBuster = 1,
                        nonSelectedAvatarAlertVisible = false,
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
    fun `given avatar already deleted when delete returns 404 then uiState is updated`() = runTest {
        val avatars = avatars.selectAvatarId(avatars.first().imageId)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.deleteAvatar(any(), any()) } returns
            GravatarResult.Failure(QuickEditorError.Request(ErrorType.NotFound))

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            val avatarToDelete = avatars.first()
            coEvery { clock.getTimeMillis() } returns 1
            viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatarToDelete.imageId))
            var avatarPickerUiState = AvatarPickerUiState(
                email = email,
                emailAvatars = avatars.minus(avatarToDelete).toEmailAvatars(),
                error = null,
                profile = ComponentState.Loaded(profile),
                avatarPickerContentLayout = avatarPickerContentLayout,
                scrollToIndex = 0,
                avatarCacheBuster = 0,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )

            avatarPickerUiState = avatarPickerUiState.copy(
                avatarCacheBuster = 1,
            )
            assertEquals(
                avatarPickerUiState,
                awaitItem(),
            )
            skipItems(1) // Skip the state update because there's no avatar selected
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when download queued then AvatarDownloadStarted sent`() = runTest {
        val avatars = avatars.selectAvatarId("1")
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)
        coEvery { imageDownloader.downloadImage(any()) } returns GravatarResult.Success(Unit)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.DownloadAvatarTapped(this@AvatarPickerViewModelTest.avatars.first()))

        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarDownloadStarted, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when download manager disabled then uiState updated`() = runTest {
        val emailAvatarsCopy = avatars.selectAvatarId("1")
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(emailAvatarsCopy)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)
        coEvery {
            imageDownloader.downloadImage(any())
        } returns GravatarResult.Failure(DownloadManagerError.DOWNLOAD_MANAGER_DISABLED)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.DownloadAvatarTapped(avatars.first()))
            assertEquals(true, awaitItem().downloadManagerDisabled)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar when download manager not available then DownloadManagerNotAvailable sent`() = runTest {
        val avatars = avatars.selectAvatarId("1")
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery { avatarRepository.selectAvatar(any(), any()) } returns GravatarResult.Success(Unit)
        coEvery {
            imageDownloader.downloadImage(any())
        } returns GravatarResult.Failure(DownloadManagerError.DOWNLOAD_MANAGER_NOT_AVAILABLE)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.onEvent(AvatarPickerEvent.DownloadAvatarTapped(avatars.first()))

        viewModel.actions.test {
            assertEquals(AvatarPickerAction.DownloadManagerNotAvailable, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatarId when updateAvatar succeeds then uiState is updated`() = runTest {
        val avatarId = "avatarId"
        val rating = Avatar.Rating.PG
        val oldAvatar = createAvatar(avatarId, isSelected = true)
        val avatars = listOf(oldAvatar)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        val updatedAvatar = oldAvatar.copy(rating)
        coEvery {
            avatarRepository.updateAvatar(email, avatarId, rating)
        } returns GravatarResult.Success(updatedAvatar)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarRatingSelected(avatarId, rating))
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = listOf(updatedAvatar).toEmailAvatars(),
                    profile = ComponentState.Loaded(profile),
                    avatarPickerContentLayout = avatarPickerContentLayout,
                    scrollToIndex = 0,
                    avatarCacheBuster = 0,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarRatingUpdated, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatarId when updateAvatar fails then uiState is reverted`() = runTest {
        val avatarId = "avatarId"
        val rating = Avatar.Rating.PG
        val oldAvatar = createAvatar(avatarId, isSelected = true)
        val avatars = listOf(oldAvatar)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.updateAvatar(email, avatarId, rating)
        } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarRatingSelected(avatarId, rating))
            val updatedAvatar = oldAvatar.copy(rating)
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = listOf(updatedAvatar).toEmailAvatars(),
                    profile = ComponentState.Loaded(profile),
                    avatarPickerContentLayout = avatarPickerContentLayout,
                    scrollToIndex = 0,
                    avatarCacheBuster = 0,
                ),
                awaitItem(),
            )
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = avatars.toEmailAvatars(),
                    profile = ComponentState.Loaded(profile),
                    avatarPickerContentLayout = avatarPickerContentLayout,
                    scrollToIndex = 0,
                    avatarCacheBuster = 0,
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AvatarPickerAction.AvatarRatingUpdateFailed, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given rating and altText not changed when updateAvatar then avatar not updated`() = runTest {
        val avatarId = "avatarId"
        val rating = Avatar.Rating.PG
        val altText = "New Alt"
        val oldAvatar = createAvatar(avatarId, isSelected = true, rating = rating, altText = altText)
        val avatars = listOf(oldAvatar)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)
        coEvery {
            avatarRepository.updateAvatar(email, avatarId, rating)
        } returns GravatarResult.Success(oldAvatar)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()
            viewModel.onEvent(AvatarPickerEvent.AvatarRatingSelected(avatarId, rating))
            expectNoEvents()
        }
        viewModel.actions.test {
            expectNoEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given empty avatar list when loaded then avatar not selected banner invisible`() = runTest {
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(false, awaitItem().nonSelectedAvatarAlertVisible)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given AvatarAltTextTapped event when received then LaunchAvatarAltText action is launched`() = runTest {
        val avatar = createAvatar("1", isSelected = true)
        val avatars = listOf(avatar)
        coEvery { avatarRepository.refreshAvatars(email) } returns GravatarResult.Success(avatars)
        coEvery { profileService.retrieveCatching(email) } returns GravatarResult.Success(profile)

        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.actions.test {
            viewModel.onEvent(AvatarPickerEvent.AvatarAltTextTapped(avatar.imageId))
            assertEquals(AvatarPickerAction.LaunchAvatarAltText(email, avatar), awaitItem())
        }

        viewModel.uiState.test {
            assertEquals(
                AvatarPickerUiState(
                    email = email,
                    emailAvatars = avatars.toEmailAvatars(),
                    profile = ComponentState.Loaded(profile),
                    avatarPickerContentLayout = avatarPickerContentLayout,
                    scrollToIndex = 0,
                    avatarCacheBuster = 0,
                ),
                awaitItem(),
            )
        }
    }

    private fun List<Avatar>.selectAvatarId(avatarId: String) = map {
        it.copy(selected = it.imageId == avatarId)
    }

    private fun initViewModel(handleExpiredSession: Boolean = true) = AvatarPickerViewModel(
        email = email,
        handleExpiredSession = handleExpiredSession,
        avatarPickerContentLayout = avatarPickerContentLayout,
        profileService = profileService,
        avatarRepository = avatarRepository,
        fileUtils = fileUtils,
        imageDownloader = imageDownloader,
        clock = clock,
    )
}
