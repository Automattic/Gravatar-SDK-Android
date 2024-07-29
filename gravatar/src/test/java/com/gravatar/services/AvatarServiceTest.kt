package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.File

class AvatarServiceTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var avatarService: AvatarService
    private val oauthToken = "oauthToken"

    @Before
    fun setUp() {
        avatarService = AvatarService()
    }

    // V1 Deprecated Methods
    @Test
    fun `given a file, email and wordpressBearerToken when uploading avatar then Gravatar service is invoked`() =
        runTest {
            val mockResponse = mockk<Response<ResponseBody>>()
            coEvery { containerRule.gravatarApiMock.uploadImage(any(), any(), any()) } returns mockResponse
            every { mockResponse.isSuccessful } returns true

            val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

            coVerify(exactly = 1) {
                containerRule.gravatarApiMock.uploadImage(
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
    fun `given gravatar update when an error occurs then Gravatar returns an error`() = runTest {
        val mockResponse = mockk<Response<ResponseBody>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 100
        }
        coEvery { containerRule.gravatarApiMock.uploadImage(any(), any(), any()) } returns mockResponse

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given gravatar update when an Exception occurs then Gravatar returns an error`() = runTest {
        coEvery { containerRule.gravatarApiMock.uploadImage(any(), any(), any()) } throws Exception()

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    // V3 Methods
    @Test
    fun `given a file when uploading avatar then Gravatar service is invoked`() = runTest {
        val mockResponse = mockk<Response<Unit>>()
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any()) } returns mockResponse
        every { mockResponse.isSuccessful } returns true

        avatarService.upload(File("avatarFile"), oauthToken)

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
            )
        }
    }

    @Test(expected = HttpException::class)
    fun `given an avatar upload when an error occurs then an exception is thrown`() = runTest {
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 500
        }
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any()) } returns mockResponse

        avatarService.upload(File("avatarFile"), oauthToken)
    }

    @Test
    fun `given a file when uploadCatching avatar then Gravatar service is invoked`() = runTest {
        val mockResponse = mockk<Response<Unit>>()
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any()) } returns mockResponse
        every { mockResponse.isSuccessful } returns true

        val response = avatarService.uploadCatching(File("avatarFile"), oauthToken)

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
            )
        }

        assertTrue(response is Result.Success)
    }

    @Test
    fun `given an avatar uploadCatching when an error occurs then a Result Failure is returned`() = runTest {
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 500
        }
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any()) } returns mockResponse

        val response = avatarService.uploadCatching(File("avatarFile"), oauthToken)

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
            )
        }

        assertEquals(ErrorType.SERVER, (response as Result.Failure).error)
    }
}
