package com.gravatar.quickeditor.ui.editor

import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.time.Clock
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuickEditorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileRepository = mockk<ProfileRepository>()
    private val clock = mockk<Clock>()

    private lateinit var viewModel: QuickEditorViewModel

    private val email = Email("testEmail")
    private val profile = defaultProfile(hash = "hash", displayName = "Display name")

    @Before
    fun setup() {
        coEvery {
            profileRepository.getProfile(email)
        } returns GravatarResult.Failure(QuickEditorError.Request(ErrorType.Server))
        coEvery { clock.getTimeMillis() } returns 0
    }

    @Test
    fun `given view model initialization when fetch profile successful then uiState is updated`() = runTest {
        coEvery { profileRepository.getProfile(email) } returns GravatarResult.Success(profile)

        viewModel = initViewModel()

        viewModel.uiState.test {
            val avatarPickerUiState = QuickEditorUiState(
                email = email,
                avatarCacheBuster = 0,
                page = QuickEditorPage.AboutEditor,
                pageNavigationEnabled = false,
            )
            assertEquals(avatarPickerUiState, awaitItem())
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
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
            val avatarPickerUiState = QuickEditorUiState(
                email = email,
                avatarCacheBuster = 0,
                page = QuickEditorPage.AboutEditor,
                pageNavigationEnabled = false,
            )
            assertEquals(avatarPickerUiState, awaitItem())
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    profile = ComponentState.Loading,
                ),
                awaitItem(),
            )
            assertEquals(
                avatarPickerUiState.copy(
                    email = email,
                    profile = null,
                ),
                awaitItem(),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given profile loaded when refresh then fetch profile not called`() = runTest {
        coEvery { profileRepository.getProfile(email) } returns GravatarResult.Success(profile)
        viewModel = initViewModel()
        advanceUntilIdle()

        viewModel.onEvent(QuickEditorEvent.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 1) { profileRepository.getProfile(email) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given avatar updated when UpdateAvatarCache event then cache buster updated`() = runTest {
        coEvery { profileRepository.getProfile(email) } returns GravatarResult.Success(profile)
        viewModel = initViewModel()
        advanceUntilIdle()

        coEvery { clock.getTimeMillis() } returns 1L
        viewModel.onEvent(QuickEditorEvent.UpdateAvatarCache)

        viewModel.uiState.test {
            assertEquals(1L, awaitItem().avatarCacheBuster)
        }
    }

    @Test
    fun `given initial state when OnEditAboutClicked event then navigates to ABOUT_EDITOR page`() = runTest {
        viewModel = initViewModel(
            initialPage = QuickEditorPage.AvatarPicker,
            navigationEnabled = true,
        )

        viewModel.onEvent(QuickEditorEvent.OnEditAboutClicked)

        viewModel.uiState.test {
            assertEquals(QuickEditorPage.AboutEditor, awaitItem().page)
        }
    }

    @Test
    fun `given initial state when OnEditAvatarClicked event then navigates to AVATAR_PICKER page`() = runTest {
        viewModel = initViewModel(
            initialPage = QuickEditorPage.AboutEditor,
            navigationEnabled = true,
        )

        viewModel.onEvent(QuickEditorEvent.OnEditAvatarClicked)

        viewModel.uiState.test {
            assertEquals(QuickEditorPage.AvatarPicker, awaitItem().page)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given unsaved changes present when OnConfirmDismissal event then alert dialog shown action is sent`() =
        runTest {
            viewModel = initViewModel(initialPage = QuickEditorPage.AvatarPicker)

            viewModel.onEvent(QuickEditorEvent.OnAboutEditorUnsavedChangesUpdated(true))
            viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(true, awaitItem().discardAboutEditorChangesDialogVisible)
            }
        }

    @Test
    fun `given no unsaved changes when OnConfirmDismissal event then DismissEditor action is sent`() = runTest {
        viewModel = initViewModel(initialPage = QuickEditorPage.AboutEditor)

        viewModel.actions.test {
            viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal)

            assertEquals(QuickEditorAction.DismissEditor, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given unsaved changes dialog shown when keep editing tapped then hidden`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(QuickEditorEvent.OnAboutEditorUnsavedChangesUpdated(true))
        viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal)

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(true, awaitItem().discardAboutEditorChangesDialogVisible)

            viewModel.onEvent(QuickEditorEvent.OnUnsavedChangesKeepEditingClicked)
            assertEquals(false, awaitItem().discardAboutEditorChangesDialogVisible)
        }

        viewModel.actions.test {
            assertEquals(QuickEditorAction.NotifyDismissIgnored, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given unsaved changes dialog shown when exit tapped then editor closed`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(QuickEditorEvent.OnAboutEditorUnsavedChangesUpdated(true))
        viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal)

        advanceUntilIdle()

        viewModel.uiState.test {
            Assert.assertEquals(true, awaitItem().discardAboutEditorChangesDialogVisible)

            viewModel.onEvent(QuickEditorEvent.OnUnsavedChangesExitClicked)
            Assert.assertEquals(false, awaitItem().discardAboutEditorChangesDialogVisible)
        }
        viewModel.actions.test {
            Assert.assertEquals(QuickEditorAction.DismissEditor, awaitItem())
        }
    }

    private fun initViewModel(
        navigationEnabled: Boolean = false,
        initialPage: QuickEditorPage = QuickEditorPage.AboutEditor,
    ) = QuickEditorViewModel(
        email = email,
        profileRepository = profileRepository,
        clock = clock,
        initialPage = initialPage,
        navigationEnabled = navigationEnabled,
        compactWindowEnabled = false,
    )
}
