package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.restapi.models.Profile
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
import retrofit2.Response
import java.util.concurrent.TimeoutException
import com.gravatar.api.models.Profile as LegacyProfile

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
        val mockResponse = mockk<Response<LegacyProfile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username) } returns mockResponse

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an username when loading its profile but data is NOT returned then result is UNKNOWN failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<LegacyProfile>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }
            coEvery { containerRule.gravatarApiServiceMock.getProfileById(username) } returns mockResponse

            val loadProfileResponse = profileService.fetchByUsername(username)

            coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
            assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
        }

    @Test
    fun `given an username when loading its profile and response is NOT successful then result is failure`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<LegacyProfile>> {
            every { isSuccessful } returns false
        }
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username) } returns mockResponse

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given an username when loading its profile and an exception is thrown then result is failure`() = runTest {
        val username = "username"
        coEvery { containerRule.gravatarApiServiceMock.getProfileById(username) } throws Exception()

        val loadProfileResponse = profileService.fetchByUsername(username)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(username) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given a hash when loading its profile and data is returned then result is successful`() = runTest {
        val usernameHash = Hash("username")
        val mockResponse = mockk<Response<LegacyProfile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiServiceMock.getProfileById(usernameHash.toString())
        } returns mockResponse

        val loadProfileResponse = profileService.fetch(usernameHash)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(usernameHash.toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an email when loading its profile and data is returned then result is successful`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        val mockResponse = mockk<Response<LegacyProfile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiServiceMock.getProfileById(usernameEmail.hash().toString())
        } returns mockResponse

        val loadProfileResponse = profileService.fetch(usernameEmail)

        coVerify(exactly = 1) { containerRule.gravatarApiServiceMock.getProfileById(usernameEmail.hash().toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    /*
     Same tests as before but using the "retrieve" method so using new models.
     Previous tests should be removed when the deprecated code is removed
     */

    // Catching Version of the methods
    @Test
    fun `given an username when retrieving its profile and data is returned then result is successful`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

        val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an username when retrieving its profile but data is NOT returned then result is UNKNOWN failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }
            coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

            val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

            coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
            assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
        }

    @Test
    fun `given an username when retrieving its profile and response is NOT successful then result is failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns false
            }
            coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

            val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

            coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
            assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
        }

    @Test
    fun `given a hash when retrieving its profile and data is returned then result is successful`() = runTest {
        val usernameHash = Hash("username")
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameHash.toString())
        } returns mockResponse

        val loadProfileResponse = profileService.retrieveCatching(usernameHash)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameHash.toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an email when retrieving its profile and data is returned then result is successful`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
        } returns mockResponse

        val loadProfileResponse = profileService.retrieveCatching(usernameEmail)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString()) }
        assertTrue(loadProfileResponse is Result.Success)
    }

    @Test
    fun `given an username when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val username = "username"
        coEvery { containerRule.gravatarApiMock.getProfileById(username) } throws Exception()

        val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given a hash when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameEmail.toString())
        } throws Exception()

        val loadProfileResponse = profileService.retrieveCatching(usernameEmail)
        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString()) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    @Test
    fun `given an email when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val usernameHash = Hash("username")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameHash.toString())
        } throws Exception()

        val loadProfileResponse = profileService.retrieveCatching(usernameHash)
        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameHash.toString()) }
        assertTrue((loadProfileResponse as Result.Failure).error == ErrorType.UNKNOWN)
    }

    // Throwing Exception Version of the methods
    @Test(expected = TimeoutException::class)
    fun `given an username when retrieving its profile and a timeout occurs then exception is thrown`() = runTest {
        val username = "username"
        coEvery { containerRule.gravatarApiMock.getProfileById(username) } throws TimeoutException()

        profileService.retrieveByUsername(username)
    }

    @Test(expected = TimeoutException::class)
    fun `given a hash when retrieving its profile and a timeout occurs then exception is thrown`() = runTest {
        val usernameHash = Hash("username")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameHash.toString())
        } throws TimeoutException()

        profileService.retrieve(usernameHash)
    }

    @Test(expected = TimeoutException::class)
    fun `given an email when retrieving its profile and a timeout occurs then exception is thrown`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
        } throws TimeoutException()

        profileService.retrieve(usernameEmail)
    }
}
