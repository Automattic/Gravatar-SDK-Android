package com.gravatar.quickeditor.data.service

import com.gravatar.quickeditor.data.models.WordPressOAuthToken
import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class WordPressOAuthServiceTest {
    private val wordPressOAuthApi = mockk<WordPressOAuthApi>()

    private lateinit var dispatcher: TestDispatcher
    private lateinit var wordPressOAuthService: WordPressOAuthService

    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        wordPressOAuthService = WordPressOAuthService(wordPressOAuthApi, dispatcher)
    }

    @Test
    fun `given oAuth params when getting the access token and an http error occurs then Failure is returned`() =
        runTest {
            val mockResponse = mockk<Response<WordPressOAuthToken>> {
                every { isSuccessful } returns false
                every { errorBody() } returns mockk(relaxed = true)
                every { code() } returns 400
            }
            coEvery {
                wordPressOAuthApi.getToken(any(), any(), any(), any(), any())
            } returns mockResponse

            val result = wordPressOAuthService.getAccessToken("code", "clientId", "clientSecret", "redirectUri")

            assertEquals(Result.Failure<WordPressOAuthToken, ErrorType>(ErrorType.Server), result)
        }

    @Test
    fun `given oAuth params when getting the access token and the body is null then Failure is returned`() = runTest {
        val mockResponse = mockk<Response<WordPressOAuthToken>> {
            every { isSuccessful } returns true
            every { body() } returns null
        }
        coEvery {
            wordPressOAuthApi.getToken(any(), any(), any(), any(), any())
        } returns mockResponse

        val result = wordPressOAuthService.getAccessToken("code", "clientId", "clientSecret", "redirectUri")

        assertEquals(Result.Failure<WordPressOAuthToken, ErrorType>(ErrorType.Unknown), result)
    }

    @Test
    fun `given oAuth params when getting the access token and the body is not null then Success is returned`() =
        runTest {
            val responseBody = WordPressOAuthToken("token", "type")
            val mockResponse = mockk<Response<WordPressOAuthToken>> {
                every { isSuccessful } returns true
                every { body() } returns responseBody
            }
            coEvery {
                wordPressOAuthApi.getToken(any(), any(), any(), any(), any())
            } returns mockResponse

            val result = wordPressOAuthService.getAccessToken("code", "clientId", "clientSecret", "redirectUri")

            assertEquals(Result.Success<String, ErrorType>(responseBody.token), result)
        }
}
