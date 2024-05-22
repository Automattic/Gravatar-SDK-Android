package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.GravatarSdkContainerRule.Companion.DEFAULT_API_KEY
import com.gravatar.api.models.Profile
import com.gravatar.types.Email
import com.gravatar.types.Hash
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class ProfileServiceTests {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var profileService: ProfileService

    @Before
    fun setUp() {
        profileService = ProfileService()
    }

    @Test
    fun `given an username when loading its profile and data is returned then result is successful`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username, any()) } returns mockResponse

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an username when loading its profile but data is NOT returned then result is UNKNOWN failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }
            coEvery { containerRule.gravatarApiServiceMock.getProfileById(username, any()) } returns mockResponse

            val loadProfileResponse = profileService.fetchByUsername(username)

            coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
            assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
        }

    @Test
    fun `given an username when loading its profile and response is NOT successful then result is failure`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns false
        }
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username, any()) } returns mockResponse

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given an username when loading its profile and an exception is thrown then result is failure`() = runTest {
        val username = "username"
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username, any()) } throws Exception()

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given a hash when loading its profile and data is returned then result is successful`() = runTest {
        val usernameHash = Hash("username")
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiServiceMock.getProfileById(usernameHash.toString(), any())
        } returns mockResponse

        val loadProfileResponse = profileService.fetch(usernameHash)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(usernameHash.toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an email when loading its profile and data is returned then result is successful`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiServiceMock.getProfileById(usernameEmail.hash().toString(), any())
        } returns mockResponse

        val loadProfileResponse = profileService.fetch(usernameEmail)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(usernameEmail.hash().toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an username and an api key when loading the profile then api key is sent and result is successful`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns true
                every { body() } returns mockk()
            }
            containerRule.withApiKey()
            coEvery {
                containerRule.gravatarApiServiceMock.getProfileById(
                    username,
                    "Bearer $DEFAULT_API_KEY",
                )
            } returns mockResponse

            val loadProfileResponse = profileService.fetchByUsername(username)

            coVerify(exactly = 1) {
                containerRule.gravatarApiServiceMock.getProfileById(
                    username,
                    "Bearer $DEFAULT_API_KEY",
                )
            }
            assertTrue(loadProfileResponse is Result.Success)
        }
}
