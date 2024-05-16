package com.gravatar

import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.Result
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(RobolectricTestRunner::class)
class AvatarServiceTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var avatarService: AvatarService

    @Before
    fun setUp() {
        avatarService = AvatarService()
    }

    @Test
    fun `given a file, email and wordpressBearerToken when uploading avatar then Gravatar service is invoked`() =
        runTest {
            val mockResponse = mockk<Response<ResponseBody>>()
            coEvery { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns mockResponse
            every { mockResponse.isSuccessful } returns true

            val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

            coVerify(exactly = 1) {
                containerRule.gravatarApiServiceMock.uploadImage(
                    "Bearer wordpressBearerToken",
                    withArg {
                        assertTrue(
                            it.headers?.values("Content-Disposition").toString().contains("account"),
                        )
                    },
                    withArg {
                        assertTrue(
                            with(it.headers?.values("Content-Disposition").toString()) {
                                contains("filedata") && contains("avatarFile")
                            },
                        )
                    },
                )
            }
            assertTrue(uploadResponse is Result.Success)
        }

    @Test
    fun `given gravatar update when a unknown error occurs then Gravatar returns UNKNOWN error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            100,
            ErrorType.UNKNOWN,
        )

    @Test
    fun `given gravatar update when a server error occurs then Gravatar returns SERVER error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            HttpResponseCode.SERVER_ERRORS.random(),
            ErrorType.SERVER,
        )

    @Test
    fun `given gravatar update when a timeout occurs then Gravatar returns TIMEOUT error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            HttpResponseCode.HTTP_CLIENT_TIMEOUT,
            ErrorType.TIMEOUT,
        )

    @Test
    fun `given gravatar update when a SocketTimeoutException occurs then Gravatar returns TIMEOUT error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            SocketTimeoutException(),
            ErrorType.TIMEOUT,
        )

    @Test
    fun `given gravatar update when a UnknownHostException occurs then Gravatar returns NETWORK error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            UnknownHostException(),
            ErrorType.NETWORK,
        )

    @Test
    fun `given gravatar update when a Exception occurs then Gravatar returns UNKNOWN error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            Exception(),
            ErrorType.UNKNOWN,
        )

    @Suppress("Cast")
    private fun `given a gravatar update when a error occurs then Gravatar returns the expected error`(
        httpResponseCode: Int,
        expectedErrorType: ErrorType,
    ) = runTest {
        val mockResponse = mockk<Response<ResponseBody>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns httpResponseCode
        }
        coEvery { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns mockResponse

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == expectedErrorType)
    }

    private fun `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
        exception: Throwable,
        expectedErrorType: ErrorType,
    ) = runTest {
        coEvery { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } throws exception

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == expectedErrorType)
    }
}
