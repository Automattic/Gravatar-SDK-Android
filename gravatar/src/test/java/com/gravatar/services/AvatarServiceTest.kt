package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Hash
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
import java.net.URI

class AvatarServiceTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var avatarService: AvatarService
    private val oauthToken = "oauthToken"

    private val avatar = Avatar {
        imageId = "id"
        imageUrl = URI.create("https://gravatar.com/avatar/test")
        selected = false
        altText = ""
        rating = Avatar.Rating.G
        updatedDate = ""
    }

    @Before
    fun setUp() {
        avatarService = AvatarService()
    }

    // V3 Methods
    @Test
    fun `given a file when uploading avatar then Gravatar service is invoked`() = runTest {
        val mockResponse = mockk<Response<Avatar>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns avatar
        }
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any(), any(), any()) } returns mockResponse
        every { mockResponse.isSuccessful } returns true

        avatarService.upload(
            File("avatarFile"),
            Hash("hash"),
            selectAvatar = false,
            oauthToken,
        )

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
                withArg { },
                withArg { },
            )
        }
    }

    @Test
    fun `given an avatar upload when an error occurs then an exception is thrown`() =
        runTestExpectingGravatarException(ErrorType.Server, HttpException::class.java) {
            val mockResponse = mockk<Response<Avatar>>(relaxed = true) {
                every { isSuccessful } returns false
                every { code() } returns 500
            }
            coEvery { containerRule.gravatarApiMock.uploadAvatar(any(), any(), any()) } returns mockResponse

            avatarService.upload(
                File("avatarFile"),
                Hash("hash"),
                selectAvatar = false,
                oauthToken,
            )
        }

    @Test
    fun `given a file when uploadCatching avatar then Gravatar service is invoked`() = runTest {
        val mockResponse = mockk<Response<Avatar>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns avatar
        }
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any(), any(), any()) } returns mockResponse

        val response = avatarService.uploadCatching(
            File("avatarFile"),
            Hash("hash"),
            selectAvatar = false,
            oauthToken,
        )

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
                withArg { },
                withArg { },
            )
        }

        assertTrue(response is GravatarResult.Success)
    }

    @Test
    fun `given an avatar uploadCatching when an error occurs then a Result Failure is returned`() = runTest {
        val mockResponse = mockk<Response<Avatar>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 500
        }
        coEvery { containerRule.gravatarApiMock.uploadAvatar(any(), any(), any()) } returns mockResponse

        val response = avatarService.uploadCatching(
            File("avatarFile"),
            Hash("hash"),
            selectAvatar = false,
            oauthToken,
        )

        coVerify(exactly = 1) {
            containerRule.gravatarApiMock.uploadAvatar(
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("image") && contains("avatarFile")
                        },
                    )
                },
                withArg { },
                withArg { },
            )
        }

        assertEquals(ErrorType.Server, (response as GravatarResult.Failure).error)
    }

    @Test
    fun `given a hash and an avatarId when setting avatar then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val avatarId = "avatarId"
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns true
        }

        coEvery { containerRule.gravatarApiMock.setEmailAvatar(avatarId, any()) } returns mockResponse

        avatarService.setAvatar(hash, avatarId, oauthToken)
    }

    @Test
    fun `given a hash and an avatarId when setting an avatar and an error occurs then an exception is thrown`() =
        runTestExpectingGravatarException(ErrorType.Server, HttpException::class.java) {
            val hash = "hash"
            val avatarId = "avatarId"
            val mockResponse = mockk<Response<Unit>>(relaxed = true) {
                every { isSuccessful } returns false
                every { code() } returns 500
            }

            coEvery { containerRule.gravatarApiMock.setEmailAvatar(avatarId, any()) } returns mockResponse

            avatarService.setAvatar(hash, avatarId, oauthToken)
        }

    @Test
    fun `given a hash and an avatarId when setAvatarCatching then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val avatarId = "avatarId"
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns true
        }

        coEvery { containerRule.gravatarApiMock.setEmailAvatar(avatarId, any()) } returns mockResponse

        val response = avatarService.setAvatarCatching(hash, avatarId, oauthToken)

        assertEquals(Unit, (response as GravatarResult.Success).value)
    }

    @Test
    fun `given a hash and an avatarId when setAvatarCatching and an error occurs then a Result Failure is returned`() =
        runTest {
            val hash = "hash"
            val avatarId = "avatarId"
            val mockResponse = mockk<Response<Unit>>(relaxed = true) {
                every { isSuccessful } returns false
                every { code() } returns 500
            }

            coEvery { containerRule.gravatarApiMock.setEmailAvatar(avatarId, any()) } returns mockResponse

            val response = avatarService.setAvatarCatching(hash, avatarId, oauthToken)

            assertEquals(ErrorType.Server, (response as GravatarResult.Failure).error)
        }
}
