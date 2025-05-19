package com.gravatar.quickeditor.ui.abouteditor

import app.cash.turbine.test
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.editor.AboutInputField
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AboutEditorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileRepository = mockk<ProfileRepository>()
    private lateinit var viewModel: AboutEditorViewModel

    private val email = Email("testEmail")
    private val profile = defaultProfile(hash = "hash")
    private val updatedProfile = defaultProfile(hash = "hash", displayName = "Updated Name")
    private val updateProfileRequest = UpdateProfileRequest {
        displayName = "Updated Name"
        jobTitle = ""
        company = ""
        description = ""
        location = ""
        pronunciation = ""
        pronouns = ""
    }
    private val visibleAboutFields = AboutInputField.all

    @Before
    fun setup() {
        coEvery { profileRepository.getProfile(email) } returns GravatarResult.Success(profile)
    }

    @Test
    fun `given view model initialization when profile fetch succeeds then uiState is updated`() = runTest {
        viewModel = initViewModel()

        viewModel.uiState.test {
            assertEquals(AboutEditorUiState(isLoading = false), awaitItem())
            assertEquals(AboutEditorUiState(isLoading = true), awaitItem())
            assertEquals(
                AboutEditorUiState(
                    isLoading = false,
                    aboutFields = profile.aboutFields(visibleAboutFields),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `given view model initialization when profile fetch fails then uiState is updated`() = runTest {
        coEvery { profileRepository.getProfile(email) } returns GravatarResult.Failure(
            QuickEditorError.Unknown,
        )

        viewModel = initViewModel()

        viewModel.uiState.test {
            assertEquals(AboutEditorUiState(isLoading = false), awaitItem())
            assertEquals(AboutEditorUiState(isLoading = true), awaitItem())
            assertEquals(AboutEditorUiState(isLoading = false), awaitItem())
        }
    }

    @Test
    fun `given updated about field when save clicked and update succeeds then uiState is updated`() = runTest {
        coEvery {
            profileRepository.updateProfile(email, updateProfileRequest)
        } returns GravatarResult.Success(updatedProfile)

        viewModel = initViewModel()
        viewModel.onEvent(
            AboutEditorEvent.OnAboutFieldUpdated(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "Updated Name",
                ),
            ),
        )

        advanceUntilIdle()

        viewModel.onEvent(AboutEditorEvent.OnSaveClicked)

        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(
                AboutEditorUiState(
                    savingProfile = true,
                    aboutFields = updatedProfile.aboutFields(visibleAboutFields),
                ),
                awaitItem(),
            )
            assertEquals(
                AboutEditorUiState(
                    savingProfile = false,
                    aboutFields = updatedProfile.aboutFields(visibleAboutFields),
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AboutEditorAction.ProfileUpdated(updatedProfile), awaitItem())
        }
    }

    @Test
    fun `given updated about field when save clicked and update fails then uiState is updated`() = runTest {
        coEvery {
            profileRepository.updateProfile(email, updateProfileRequest)
        } returns GravatarResult.Failure(QuickEditorError.Unknown)

        viewModel = initViewModel()
        viewModel.onEvent(
            AboutEditorEvent.OnAboutFieldUpdated(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "Updated Name",
                ),
            ),
        )

        advanceUntilIdle()

        viewModel.onEvent(AboutEditorEvent.OnSaveClicked)

        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(
                AboutEditorUiState(
                    savingProfile = true,
                    aboutFields = updatedProfile.aboutFields(visibleAboutFields),
                ),
                awaitItem(),
            )
            assertEquals(
                AboutEditorUiState(
                    savingProfile = false,
                    aboutFields = updatedProfile.aboutFields(visibleAboutFields),
                ),
                awaitItem(),
            )
        }
        viewModel.actions.test {
            assertEquals(AboutEditorAction.ProfileUpdateFailed, awaitItem())
        }
    }

    @Test
    fun `given no changes when done clicked then editor is closed`() = runTest {
        viewModel = initViewModel()

        advanceUntilIdle()

        viewModel.onEvent(AboutEditorEvent.OnDoneClicked)

        viewModel.actions.test {
            assertEquals(AboutEditorAction.CloseEditor, awaitItem())
        }
    }

    @Test
    fun `given changes when done clicked then discard changes dialog is shown`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(
            AboutEditorEvent.OnAboutFieldUpdated(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "Updated Name",
                ),
            ),
        )
        viewModel.onEvent(AboutEditorEvent.OnDoneClicked)

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(true, awaitItem().discardChangesDialogVisible)
        }
    }

    @Test
    fun `given unsaved changes dialog shown when keep editing tapped then hidden`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(
            AboutEditorEvent.OnAboutFieldUpdated(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "Updated Name",
                ),
            ),
        )
        viewModel.onEvent(AboutEditorEvent.OnDoneClicked)

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(true, awaitItem().discardChangesDialogVisible)

            viewModel.onEvent(AboutEditorEvent.OnUnsavedChangesKeepEditingClicked)
            assertEquals(false, awaitItem().discardChangesDialogVisible)
        }

        viewModel.actions.test {
            assertEquals(AboutEditorAction.NotifyDismissIgnored, awaitItem())
        }
    }

    @Test
    fun `given unsaved changes dialog shown when exit tapped then editor closed`() = runTest {
        viewModel = initViewModel()

        viewModel.onEvent(
            AboutEditorEvent.OnAboutFieldUpdated(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "Updated Name",
                ),
            ),
        )
        viewModel.onEvent(AboutEditorEvent.OnDoneClicked)

        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(true, awaitItem().discardChangesDialogVisible)

            viewModel.onEvent(AboutEditorEvent.OnUnsavedChangesExitClicked)
            assertEquals(false, awaitItem().discardChangesDialogVisible)
        }
        viewModel.actions.test {
            assertEquals(AboutEditorAction.CloseEditor, awaitItem())
        }
    }

    @Test
    fun `given only personal about section visible when profile fetched then uiState is updated`() = runTest {
        viewModel = initViewModel(aboutFields = AboutInputField.personal)

        viewModel.uiState.test {
            assertEquals(AboutEditorUiState(isLoading = false), awaitItem())
            assertEquals(AboutEditorUiState(isLoading = true), awaitItem())
            assertEquals(
                setOf(
                    AboutEditorField(
                        type = AboutInputField.DisplayName,
                        value = profile.displayName,
                        maxLines = 1,
                    ),
                    AboutEditorField(
                        type = AboutInputField.AboutMe,
                        value = profile.description,
                        maxLines = 3,
                    ),
                    AboutEditorField(
                        type = AboutInputField.Pronunciation,
                        value = profile.pronunciation,
                    ),
                    AboutEditorField(
                        type = AboutInputField.Pronouns,
                        value = profile.pronouns,
                    ),
                    AboutEditorField(
                        type = AboutInputField.Location,
                        value = profile.location,
                    ),
                ),
                awaitItem().aboutFields,
            )
        }
    }

    private fun initViewModel(aboutFields: Set<AboutInputField> = visibleAboutFields) = AboutEditorViewModel(
        email = email,
        profileRepository = profileRepository,
        visibleAboutFields = aboutFields,
    )
}
