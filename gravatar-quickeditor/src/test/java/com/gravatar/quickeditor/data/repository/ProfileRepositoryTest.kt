package com.gravatar.quickeditor.data.repository

import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.quickeditor.ui.avatarpicker.EmailAvatars
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var profileRepository: ProfileRepository
    private val profileService: ProfileService = mockk()
    private val tokenStorage: TokenStorage = mockk()

    private val email = Email("email")

    @Before
    fun setUp() {
        profileRepository = ProfileRepository(
            profileService = profileService,
            tokenStorage = tokenStorage,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `given email when token not found then TokenNotFound result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = profileRepository.getProfile(email)

        assertEquals(
            GravatarResult.Failure<EmailAvatars, QuickEditorError>(QuickEditorError.TokenNotFound),
            result,
        )
    }

    @Test
    fun `given oauthToken when retrieving profile and data is returned then result is successful`() = runTest {
        val oauthToken = "oauth"
        val profileResult = defaultProfile(hash = "hash")
        coEvery { tokenStorage.getToken(any()) } returns oauthToken
        coEvery {
            profileService.retrieveAuthenticatedCatching(oauthToken)
        } returns GravatarResult.Success(profileResult)

        val profile = profileRepository.getProfile(email)

        coVerify(exactly = 1) { profileService.retrieveAuthenticatedCatching(oauthToken) }
        assertEquals(GravatarResult.Success<Profile, QuickEditorError>(profileResult), profile)
    }

    @Test
    fun `given oauthToken when retrieving profile and response is NOT successful then Failure returned`() = runTest {
        val oauthToken = "oauth"
        coEvery { tokenStorage.getToken(any()) } returns oauthToken
        coEvery {
            profileService.retrieveAuthenticatedCatching(oauthToken)
        } returns GravatarResult.Failure(ErrorType.Server)

        val result = profileRepository.getProfile(email)

        assertEquals(
            GravatarResult.Failure<Profile, QuickEditorError>(QuickEditorError.Request(ErrorType.Server)),
            result,
        )
    }

    @Test
    fun `given email and updateProfileRequest when token not found then TokenNotFound result`() = runTest {
        val updateProfileRequest = mockk<UpdateProfileRequest>()
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = profileRepository.updateProfile(email, updateProfileRequest)

        assertEquals(
            GravatarResult.Failure<Profile, QuickEditorError>(QuickEditorError.TokenNotFound),
            result,
        )
    }

    @Test
    fun `given email and updateProfileRequest when update is successful then result is successful`() = runTest {
        val oauthToken = "oauth"
        val updateProfileRequest = mockk<UpdateProfileRequest>()
        val profileResult = defaultProfile(hash = "hash")
        coEvery { tokenStorage.getToken(any()) } returns oauthToken
        coEvery {
            profileService.updateProfileCatching(oauthToken, updateProfileRequest)
        } returns GravatarResult.Success(profileResult)

        val result = profileRepository.updateProfile(email, updateProfileRequest)

        coVerify(exactly = 1) { profileService.updateProfileCatching(oauthToken, updateProfileRequest) }
        assertEquals(GravatarResult.Success<Profile, QuickEditorError>(profileResult), result)
    }

    @Test
    fun `given email and updateProfileRequest when update fails then Failure result`() = runTest {
        val oauthToken = "oauth"
        val updateProfileRequest = mockk<UpdateProfileRequest>()
        coEvery { tokenStorage.getToken(any()) } returns oauthToken
        coEvery {
            profileService.updateProfileCatching(oauthToken, updateProfileRequest)
        } returns GravatarResult.Failure(ErrorType.Server)

        val result = profileRepository.updateProfile(email, updateProfileRequest)

        coVerify(exactly = 1) { profileService.updateProfileCatching(oauthToken, updateProfileRequest) }
        assertEquals(
            GravatarResult.Failure<Profile, QuickEditorError>(QuickEditorError.Request(ErrorType.Server)),
            result,
        )
    }
}
