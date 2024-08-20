package com.gravatar.quickeditor.data.repository

import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Identity
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.IdentityService
import com.gravatar.services.Result
import com.gravatar.types.Email
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class AvatarRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule(testDispatcher)

    private val avatarService = mockk<AvatarService>()
    private val identityService = mockk<IdentityService>()
    private val tokenStorage = mockk<TokenStorage>()

    private lateinit var avatarRepository: AvatarRepository

    private val email = Email("email")

    @Before
    fun setUp() {
        avatarRepository = AvatarRepository(
            avatarService = avatarService,
            identityService = identityService,
            tokenStorage = tokenStorage,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `given email when token not found then TokenNotFound result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = avatarRepository.getAvatars(email)

        assertEquals(Result.Failure<IdentityAvatars, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when avatar service fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"

        coEvery { avatarService.retrieve(any()) } throws Exception()

        val result = avatarRepository.getAvatars(email)

        assertEquals(Result.Failure<IdentityAvatars, QuickEditorError>(QuickEditorError.Unknown), result)
    }

    @Test
    fun `given token stored when identity service fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"

        coEvery { avatarService.retrieve(any()) } returns listOf(createAvatar("1"))
        coEvery { identityService.retrieve(any(), any()) } throws Exception()

        val result = avatarRepository.getAvatars(email)

        assertEquals(Result.Failure<IdentityAvatars, QuickEditorError>(QuickEditorError.Unknown), result)
    }

    @Test
    fun `given token stored when avatar service and identity service succeed then Success result`() = runTest {
        val imageId = "2"
        val avatar = createAvatar(imageId)
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.retrieve(any()) } returns listOf(avatar)
        coEvery { identityService.retrieve(any(), any()) } returns createIdentity(imageId)

        val result = avatarRepository.getAvatars(email)

        assertEquals(
            Result.Success<IdentityAvatars, QuickEditorError>(IdentityAvatars(listOf(avatar), imageId)),
            result,
        )
    }

    @Test
    fun `given email stored when token not found then TokenNotFound result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(Result.Failure<String, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when avatar selected fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { identityService.setAvatarCatching(any(), any(), any()) } returns Result.Failure(ErrorType.UNKNOWN)

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(Result.Failure<String, QuickEditorError>(QuickEditorError.Server), result)
    }

    @Test
    fun `given token stored when avatar selected succeeds then Success result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { identityService.setAvatarCatching(any(), any(), any()) } returns Result.Success(Unit)

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(Result.Success<Unit, QuickEditorError>(Unit), result)
    }

    private fun createAvatar(id: String) = Avatar {
        imageUrl = "/image/url"
        format = 0
        imageId = id
        rating = "G"
        altText = "alt"
        isCropped = true
        updatedDate = Instant.now()
    }

    private fun createIdentity(imageIdentifier: String) = Identity {
        id = "id"
        email = "email"
        imageId = imageIdentifier
        rating = "G"
        imageUrl = "url"
        emailHash = "hash"
        format = 1
    }
}
