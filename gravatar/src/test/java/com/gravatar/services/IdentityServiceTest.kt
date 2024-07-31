package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.restapi.models.Identity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class IdentityServiceTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var identityService: IdentityService
    private val oauthToken = "oauthToken"

    @Before
    fun setUp() {
        identityService = IdentityService()
    }

    @Test
    fun `given a hash when retrieving identity then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val identity = mockk<Identity>()
        val mockResponse = mockk<Response<Identity>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns identity
        }

        coEvery { containerRule.gravatarApiMock.getIdentity(hash) } returns mockResponse

        val response = identityService.retrieve(hash, oauthToken)

        assertEquals(identity, response)
    }

    @Test(expected = HttpException::class)
    fun `given a hash when retrieving an identity and an error occurs then an exception is thrown`() = runTest {
        val hash = "hash"
        val mockResponse = mockk<Response<Identity>>(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns 500
        }

        coEvery { containerRule.gravatarApiMock.getIdentity(hash) } returns mockResponse

        identityService.retrieve(hash, oauthToken)
    }

    @Test
    fun `given a hash when retrieveCatching identity then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val identity = mockk<Identity>()
        val mockResponse = mockk<Response<Identity>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns identity
        }

        coEvery { containerRule.gravatarApiMock.getIdentity(hash) } returns mockResponse

        val response = identityService.retrieveCatching(hash, oauthToken)

        assertEquals(identity, (response as Result.Success).value)
    }

    @Test
    fun `given a hash when retrieveCatching identity and an error occurs then a Result Failure is returned`() =
        runTest {
            val hash = "hash"
            val mockResponse = mockk<Response<Identity>>(relaxed = true) {
                every { isSuccessful } returns false
                every { code() } returns 500
            }

            coEvery { containerRule.gravatarApiMock.getIdentity(hash) } returns mockResponse

            val response = identityService.retrieveCatching(hash, oauthToken)

            assertEquals(ErrorType.SERVER, (response as Result.Failure).error)
        }

    @Test
    fun `given a hash and an avatarId when setting avatar then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val avatarId = "avatarId"
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns true
        }

        coEvery { containerRule.gravatarApiMock.setIdentityAvatar(hash, any()) } returns mockResponse

        identityService.setAvatar(hash, avatarId, oauthToken)
    }

    @Test(expected = HttpException::class)
    fun `given a hash and an avatarId when setting an avatar and an error occurs then an exception is thrown`() =
        runTest {
            val hash = "hash"
            val avatarId = "avatarId"
            val mockResponse = mockk<Response<Unit>>(relaxed = true) {
                every { isSuccessful } returns false
                every { code() } returns 500
            }

            coEvery { containerRule.gravatarApiMock.setIdentityAvatar(hash, any()) } returns mockResponse

            identityService.setAvatar(hash, avatarId, oauthToken)
        }

    @Test
    fun `given a hash and an avatarId when setAvatarCatching then Gravatar service is invoked`() = runTest {
        val hash = "hash"
        val avatarId = "avatarId"
        val mockResponse = mockk<Response<Unit>>(relaxed = true) {
            every { isSuccessful } returns true
        }

        coEvery { containerRule.gravatarApiMock.setIdentityAvatar(hash, any()) } returns mockResponse

        val response = identityService.setAvatarCatching(hash, avatarId, oauthToken)

        assertEquals(Unit, (response as Result.Success).value)
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

            coEvery { containerRule.gravatarApiMock.setIdentityAvatar(hash, any()) } returns mockResponse

            val response = identityService.setAvatarCatching(hash, avatarId, oauthToken)

            assertEquals(ErrorType.SERVER, (response as Result.Failure).error)
        }
}
