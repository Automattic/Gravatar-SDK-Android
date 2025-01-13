package com.gravatar.quickeditor.ui.alttext

import app.cash.turbine.test
import com.gravatar.quickeditor.createAvatar
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.GravatarResult
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.net.URI

class AltTextViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var viewModel: AltTextViewModel

    private val email = "testEmail"
    private val avatarId = "testAvatarId"
    private val altText = "Alternative Text"
    private val avatarUrl = URI("https://gravatar.com/avatar/test")
    private val avatarRepository = mockk<AvatarRepository>()

    @Test
    fun `given an avatar that can't be loaded when view model is initialized then  AvatarCantBeLoaded is sent`() =
        runTest {
            coEvery { avatarRepository.getAvatar(any(), any()) } returns null

            viewModel = AltTextViewModel(email, avatarId, avatarRepository)

            viewModel.actions.test {
                assertEquals(AltTextAction.AvatarCantBeLoaded, awaitItem())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given some initial values when view model is initialized then uiState is correct`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        advanceUntilIdle()

        viewModel.uiState.test {
            val altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = false,
                altText = altText,
                altTextMaxLength = 125,
            )

            assertEquals(altTextUiState, expectMostRecentItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an alt text change event when onEvent is called then uiState is updated with new alt text`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        val newAltText = "New Alternative Text"

        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

            val altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = false,
                altText = newAltText,
                initialAltText = altText,
                altTextMaxLength = 125,
            )

            assertEquals(altTextUiState, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an alt text change with too many char when onEvent is called then alt text unchanged`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        advanceUntilIdle()

        val altTextReachingTheLimit = "a".repeat(126)
        viewModel.onEvent(AltTextEvent.AvatarAltTextChange(altTextReachingTheLimit))
        val newAltText = "b".repeat(126)
        viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

        viewModel.uiState.test {
            val altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = false,
                altText = altTextReachingTheLimit.take(125),
                initialAltText = altText,
                altTextMaxLength = 125,
            )

            assertEquals(altTextUiState, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given new alt text that's below the limit when onEvent is called then alt text modified`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        advanceUntilIdle()

        val altTextReachingTheLimit = "a".repeat(126)
        viewModel.onEvent(AltTextEvent.AvatarAltTextChange(altTextReachingTheLimit))

        viewModel.uiState.test {
            expectMostRecentItem()

            val newAltText = "a".repeat(124)
            viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

            val altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = false,
                altText = newAltText,
                initialAltText = altText,
                altTextMaxLength = 125,
            )

            assertEquals(altTextUiState, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an alt text limit reached when onEvent is called then alt text stays unchanged`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        advanceUntilIdle()

        val newAltText = "a".repeat(126)
        viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

        viewModel.uiState.test {
            val altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = false,
                altText = newAltText.take(125),
                initialAltText = altText,
                altTextMaxLength = 125,
            )

            assertEquals(altTextUiState, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an alt text save tapped event when alt text is successfully updated then uiState is updated`() =
        runTest {
            initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

            val newAltText = "New Alternative Text"
            coEvery {
                avatarRepository.updateAvatar(email = any(), avatarId = avatarId, rating = null, altText = newAltText)
            } returns GravatarResult.Success(mockk())

            advanceUntilIdle()
            viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

            viewModel.uiState.test {
                expectMostRecentItem()

                viewModel.onEvent(AltTextEvent.AvatarAltTextSaveTapped)

                var altTextUiState = AltTextUiState(
                    avatarUrl = avatarUrl,
                    isUpdating = true,
                    altText = newAltText,
                    altTextMaxLength = 125,
                    initialAltText = altText,
                )

                assertEquals(altTextUiState, awaitItem())

                altTextUiState = altTextUiState.copy(
                    isUpdating = false,
                )

                assertEquals(altTextUiState, awaitItem())
            }

            viewModel.actions.test {
                assertEquals(AltTextAction.AltTextUpdated, awaitItem())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an alt text save tapped event when alt text update fails then uiState is updated`() = runTest {
        initWithAvatarStorage(createAvatar(id = avatarId, url = avatarUrl, altText = altText))

        val newAltText = "New Alternative Text"
        coEvery {
            avatarRepository.updateAvatar(email = any(), avatarId = avatarId, rating = null, altText = newAltText)
        } returns GravatarResult.Failure(mockk())

        advanceUntilIdle()
        viewModel.onEvent(AltTextEvent.AvatarAltTextChange(newAltText))

        viewModel.uiState.test {
            expectMostRecentItem()

            viewModel.onEvent(AltTextEvent.AvatarAltTextSaveTapped)

            var altTextUiState = AltTextUiState(
                avatarUrl = avatarUrl,
                isUpdating = true,
                altText = newAltText,
                altTextMaxLength = 125,
                initialAltText = altText,
            )

            assertEquals(altTextUiState, awaitItem())

            altTextUiState = altTextUiState.copy(
                isUpdating = false,
            )

            assertEquals(altTextUiState, awaitItem())
        }

        viewModel.actions.test {
            assertEquals(AltTextAction.AltTextUpdateFailed, awaitItem())
        }
    }

    private fun initWithAvatarStorage(avatar: Avatar) {
        coEvery { avatarRepository.getAvatar(any(), any()) } returns avatar

        viewModel = AltTextViewModel(email, avatarId, avatarRepository)
    }
}
