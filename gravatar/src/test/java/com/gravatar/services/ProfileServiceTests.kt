package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import com.gravatar.restapi.models.AssociatedResponse
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Email
import com.gravatar.types.Hash
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException

class ProfileServiceTests {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var profileService: ProfileService

    @Before
    fun setUp() {
        profileService = ProfileService()
    }

    // Catching Version of the methods
    @Test
    fun `given a username when retrieving its profile and data is returned then result is successful`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }
        coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

        val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
        assertTrue(loadProfileResponse is GravatarResult.Success)
    }

    @Test
    fun `given a username when retrieving its profile but data is NOT returned then result is UNKNOWN failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }
            coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

            val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

            coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
            assertTrue(
                (loadProfileResponse as GravatarResult.Failure).error ==
                    ErrorType.Unknown("Response body is null"),
            )
        }

    @Test
    fun `given a username when retrieving its profile and response is NOT successful then result is failure`() =
        runTest {
            val username = "username"
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns false
                every { code() } returns 418
                every { errorBody() } returns mockk(relaxed = true)
            }
            coEvery { containerRule.gravatarApiMock.getProfileById(username) } returns mockResponse

            val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

            coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
            assertTrue((loadProfileResponse as GravatarResult.Failure).error is ErrorType.Unknown)
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
        assertTrue(loadProfileResponse is GravatarResult.Success)
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
        assertTrue(loadProfileResponse is GravatarResult.Success)
    }

    @Test
    fun `given a username when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val username = "username"
        coEvery { containerRule.gravatarApiMock.getProfileById(username) } throws Exception()

        val loadProfileResponse = profileService.retrieveByUsernameCatching(username)

        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(username) }
        assertTrue((loadProfileResponse as GravatarResult.Failure).error == ErrorType.Unknown())
    }

    @Test
    fun `given a hash when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val usernameEmail = Email("username@automattic.com")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
        } throws Exception()

        val loadProfileResponse = profileService.retrieveCatching(usernameEmail)
        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString()) }
        assertTrue((loadProfileResponse as GravatarResult.Failure).error == ErrorType.Unknown())
    }

    @Test
    fun `given an email when retrieving its profile and an exception is thrown then result is failure`() = runTest {
        val usernameHash = Hash("username")
        coEvery {
            containerRule.gravatarApiMock.getProfileById(usernameHash.toString())
        } throws Exception()

        val loadProfileResponse = profileService.retrieveCatching(usernameHash)
        coVerify(exactly = 1) { containerRule.gravatarApiMock.getProfileById(usernameHash.toString()) }
        assertTrue((loadProfileResponse as GravatarResult.Failure).error == ErrorType.Unknown())
    }

    @Test
    fun `given a username when retrieving its profile which is not found then failure with NOT_FOUND`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns false
            every { errorBody() } returns mockk(relaxed = true)
            every { code() } returns 404
            every { message() } returns "Not found"
        }
        coEvery {
            containerRule.gravatarApiMock.getProfileById(username)
        } returns mockResponse

        assertEquals(ErrorType.NotFound, (profileService.retrieveCatching(username) as GravatarResult.Failure).error)
    }

    // Throwing Exception Version of the methods
    @Test
    fun `given a username when retrieving its profile and a timeout occurs then exception is thrown`() =
        runTestExpectingGravatarException(ErrorType.Timeout, SocketTimeoutException::class.java) {
            val username = "username"
            coEvery { containerRule.gravatarApiMock.getProfileById(username) } throws SocketTimeoutException()

            profileService.retrieveByUsername(username)
        }

    @Test
    fun `given a hash when retrieving its profile and a timeout occurs then exception is thrown`() =
        runTestExpectingGravatarException(ErrorType.Timeout, SocketTimeoutException::class.java) {
            val usernameHash = Hash("username")
            coEvery {
                containerRule.gravatarApiMock.getProfileById(usernameHash.toString())
            } throws SocketTimeoutException()

            profileService.retrieve(usernameHash)
        }

    @Test
    fun `given an email when retrieving its profile and a timeout occurs then exception is thrown`() =
        runTestExpectingGravatarException(ErrorType.Timeout, SocketTimeoutException::class.java) {
            val usernameEmail = Email("username@automattic.com")
            coEvery {
                containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
            } throws SocketTimeoutException()

            profileService.retrieve(usernameEmail)
        }

    @Test
    fun `given an email when retrieving its profile and the body is null then IllegalStateException is thrown`() =
        runTestExpectingGravatarException(
            ErrorType.Unknown("Response body is null"),
            IllegalStateException::class.java,
        ) {
            val usernameEmail = Email("username@automattic.com")
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }
            coEvery {
                containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
            } returns mockResponse

            profileService.retrieve(usernameEmail)
        }

    @Test
    fun `given an email when retrieving its profile and a http error occurs then HttpException is thrown`() =
        runTestExpectingGravatarException(ErrorType.Unauthorized, HttpException::class.java) {
            val usernameEmail = Email("username@automattic.com")
            val mockResponse = mockk<Response<Profile>> {
                every { isSuccessful } returns false
                every { errorBody() } returns mockk(relaxed = true)
                every { code() } returns 401
                every { message() } returns "Unauthorized"
            }
            coEvery {
                containerRule.gravatarApiMock.getProfileById(usernameEmail.hash().toString())
            } returns mockResponse

            profileService.retrieve(usernameEmail)
        }

    @Test
    fun `given a username when retrieving its profile which is not found then null is returned`() = runTest {
        val username = "username"
        val mockResponse = mockk<Response<Profile>> {
            every { isSuccessful } returns false
            every { errorBody() } returns mockk(relaxed = true)
            every { code() } returns 404
            every { message() } returns "Not found"
        }
        coEvery {
            containerRule.gravatarApiMock.getProfileById(username)
        } returns mockResponse

        assertNull(profileService.retrieve(username))
    }

    @Test
    fun `given oauthToken and email when checking associated email and it's associated then result is successful`() =
        runTest {
            val oauthToken = "oauth"
            val usernameEmail = Email("username@automattic.com")

            val body = AssociatedResponse { associated = true }
            val mockResponse = mockk<Response<AssociatedResponse>> {
                every { isSuccessful } returns true
                every { body() } returns body
            }

            coEvery {
                containerRule.gravatarApiMock.associatedEmail(usernameEmail.hash().toString())
            } returns mockResponse

            assertTrue(
                (
                    profileService.checkAssociatedEmailCatching(
                        oauthToken,
                        usernameEmail,
                    ) as GravatarResult.Success
                ).value,
            )
        }

    @Test
    fun `given oauthToken and email when checking associated email and it's not associated then successful`() =
        runTest {
            val oauthToken = "oauth"
            val usernameEmail = Email("username@automattic.com")

            val body = AssociatedResponse { associated = false }
            val mockResponse = mockk<Response<AssociatedResponse>> {
                every { isSuccessful } returns true
                every { body() } returns body
            }

            coEvery {
                containerRule.gravatarApiMock.associatedEmail(usernameEmail.hash().toString())
            } returns mockResponse

            assertFalse(
                (
                    profileService.checkAssociatedEmailCatching(
                        oauthToken,
                        usernameEmail,
                    ) as GravatarResult.Success
                ).value,
            )
        }

    @Test
    fun `given oauthToken and email when checking associated email and response body is null then result is Failure`() =
        runTest {
            val oauthToken = "oauth"
            val usernameEmail = Email("username@automattic.com")

            val mockResponse = mockk<Response<AssociatedResponse>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }

            coEvery {
                containerRule.gravatarApiMock.associatedEmail(usernameEmail.hash().toString())
            } returns mockResponse

            val result = profileService.checkAssociatedEmailCatching(oauthToken, usernameEmail)
            assertTrue((result as GravatarResult.Failure).error == ErrorType.Unknown("Response body is null"))
        }

    @Test
    fun `given oauthToken and email when checking associated email and null body then IllegalStateException thrown`() =
        runTestExpectingGravatarException(
            ErrorType.Unknown("Response body is null"),
            IllegalStateException::class.java,
        ) {
            val oauthToken = "oauth"
            val usernameEmail = Email("username@automattic.com")

            val mockResponse = mockk<Response<AssociatedResponse>> {
                every { isSuccessful } returns true
                every { body() } returns null
            }

            coEvery {
                containerRule.gravatarApiMock.associatedEmail(usernameEmail.hash().toString())
            } returns mockResponse

            profileService.checkAssociatedEmail(oauthToken, usernameEmail)
        }
}
