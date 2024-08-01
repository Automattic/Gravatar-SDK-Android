package com.gravatar.quickeditor.ui.avatarpicker

import app.cash.turbine.test
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AvatarPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val tokenStorage = mockk<TokenStorage>()
    private val avatarService = mockk<AvatarService>()

    private lateinit var viewModel: AvatarPickerViewModel

    private val email = Email("testEmail")

    @Test
    fun `given view model initialization when token present and avatars request succeed then uiState is updated`() =
        runTest {
            val avatars = listOf(mockk<Avatar>())

            coEvery { tokenStorage.getToken(email.hash().toString()) } returns "token"
            coEvery { avatarService.retrieveCatching("token") } returns Result.Success(avatars)

            viewModel = AvatarPickerViewModel(email, avatarService, tokenStorage)

            viewModel.uiState.test {
                assertEquals(AvatarPickerUiState(avatars = emptyList(), error = false), awaitItem())
                assertEquals(AvatarPickerUiState(avatars = avatars, error = false), awaitItem())
            }
        }

    @Test
    fun `given view model initialization when token present and avatars request fails then uiState is updated`() =
        runTest {
            coEvery { tokenStorage.getToken(email.hash().toString()) } returns "token"
            coEvery { avatarService.retrieveCatching("token") } returns Result.Failure(ErrorType.UNKNOWN)

            viewModel = AvatarPickerViewModel(email, avatarService, tokenStorage)

            viewModel.uiState.test {
                assertEquals(AvatarPickerUiState(avatars = emptyList(), error = false), awaitItem())
                assertEquals(AvatarPickerUiState(avatars = emptyList(), error = true), awaitItem())
            }
        }
}
