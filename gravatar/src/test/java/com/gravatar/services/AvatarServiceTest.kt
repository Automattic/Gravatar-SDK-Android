package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
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
import retrofit2.Response
import java.io.File

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
    fun `given gravatar update when an error occurs then Gravatar returns an error`() = runTest {
        val mockResponse = mockk<Response<ResponseBody>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 100
        }
        coEvery { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns mockResponse

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given gravatar update when an Exception occurs then Gravatar returns an error`() = runTest {
        coEvery { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } throws Exception()

        val uploadResponse = avatarService.upload(File("avatarFile"), Email("email"), "wordpressBearerToken")

        assertTrue((uploadResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }
}
