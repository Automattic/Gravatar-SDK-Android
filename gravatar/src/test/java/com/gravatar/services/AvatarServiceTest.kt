package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
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
