package com.gravatar.quickeditor.ui.splash

import app.cash.turbine.test
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val tokenStorage = mockk<TokenStorage>()

    private lateinit var viewModel: SplashViewModel

    private val email = Email("testEmail")

    @Test
    fun `given view model initialization when token present then ShowQuickEditor sent`() = runTest {
        coEvery { tokenStorage.getToken(email.hash().toString()) } returns "token"

        viewModel = SplashViewModel(email, tokenStorage)

        viewModel.actions.test {
            assertEquals(SplashAction.ShowQuickEditor, awaitItem())
        }
    }

    @Test
    fun `given view model initialization when token null then ShowOAuth sent`() = runTest {
        coEvery { tokenStorage.getToken(email.hash().toString()) } returns null

        viewModel = SplashViewModel(email, tokenStorage)

        viewModel.actions.test {
            assertEquals(SplashAction.ShowOAuth, awaitItem())
        }
    }
}
