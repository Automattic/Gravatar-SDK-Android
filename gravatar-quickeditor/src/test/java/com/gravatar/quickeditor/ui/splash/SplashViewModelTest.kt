package com.gravatar.quickeditor.ui.splash

import app.cash.turbine.test
import com.gravatar.quickeditor.data.storage.DataStoreTokenStorage
import com.gravatar.quickeditor.data.storage.InMemoryTokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.coVerify
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

    private val dataStoreTokenStorage = mockk<DataStoreTokenStorage>()
    private val inMemoryTokenStorage = mockk<InMemoryTokenStorage>()

    private lateinit var viewModel: SplashViewModel

    private val email = Email("testEmail")
    private val token = "token"

    @Test
    fun `given token not provided when token present in storage then ShowQuickEditor sent`() = runTest {
        coEvery { dataStoreTokenStorage.getToken(email.hash().toString()) } returns "token"

        viewModel = initViewModel(null)

        viewModel.actions.test {
            assertEquals(SplashAction.ShowQuickEditor, awaitItem())
        }
        coVerify(exactly = 0) { inMemoryTokenStorage.storeToken(any(), any()) }
    }

    @Test
    fun `given token not provided when token not present in storage then ShowOAuth sent`() = runTest {
        coEvery { dataStoreTokenStorage.getToken(email.hash().toString()) } returns null

        viewModel = initViewModel(null)

        viewModel.actions.test {
            assertEquals(SplashAction.ShowOAuth, awaitItem())
        }
        coVerify(exactly = 0) { inMemoryTokenStorage.storeToken(any(), any()) }
    }

    @Test
    fun `given token provided when initialized then token stored`() = runTest {
        coEvery { inMemoryTokenStorage.storeToken(any(), any()) } returns Unit

        viewModel = initViewModel(token)

        viewModel.actions.test {
            assertEquals(SplashAction.ShowQuickEditor, awaitItem())
        }
        coVerify(exactly = 1) { inMemoryTokenStorage.storeToken(any(), any()) }
        coVerify(exactly = 0) { dataStoreTokenStorage.getToken(email.hash().toString()) }
    }

    private fun initViewModel(token: String?): SplashViewModel {
        return SplashViewModel(email, token, inMemoryTokenStorage, dataStoreTokenStorage)
    }
}
