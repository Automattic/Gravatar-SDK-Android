package com.gravatar.quickeditor.data.repository

import android.net.Uri
import androidx.core.net.toFile
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
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

        coEvery { avatarService.retrieveCatching(any()) } returns Result.Failure(ErrorType.SERVER)
        coEvery { identityService.retrieveCatching(any(), any()) } returns Result.Success(createIdentity("1"))

        val result = avatarRepository.getAvatars(email)

        assertEquals(
            Result.Failure<IdentityAvatars, QuickEditorError>(QuickEditorError.Request(ErrorType.SERVER)),
            result,
        )
    }

    @Test
    fun `given token stored when identity service fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"

        coEvery { avatarService.retrieveCatching(any()) } returns Result.Success(listOf(createAvatar("1")))
        coEvery { identityService.retrieveCatching(any(), any()) } returns Result.Failure(ErrorType.SERVER)

        val result = avatarRepository.getAvatars(email)

        assertEquals(
            Result.Failure<IdentityAvatars, QuickEditorError>(QuickEditorError.Request(ErrorType.SERVER)),
            result,
        )
    }

    @Test
    fun `given token stored when avatar service and identity service succeed then Success result`() = runTest {
        val imageId = "2"
        val avatar = createAvatar(imageId)
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.retrieveCatching(any()) } returns Result.Success(listOf(avatar))
        coEvery { identityService.retrieveCatching(any(), any()) } returns Result.Success(createIdentity(imageId))

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

        assertEquals(Result.Failure<String, QuickEditorError>(QuickEditorError.Request(ErrorType.UNKNOWN)), result)
    }

    @Test
    fun `given token stored when avatar selected succeeds then Success result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { identityService.setAvatarCatching(any(), any(), any()) } returns Result.Success(Unit)

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(Result.Success<Unit, QuickEditorError>(Unit), result)
    }

    @Test
    fun `given token not stored when avatar upload then Failure result`() = runTest {
        val uri = mockk<Uri>()
        coEvery { tokenStorage.getToken(any()) } returns null
        coEvery { avatarService.uploadCatching(any(), any()) } returns Result.Success(Unit)

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(Result.Failure<Unit, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when avatar upload succeeds then Success result`() = runTest {
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val uri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.uploadCatching(any(), any()) } returns Result.Success(Unit)

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(Result.Success<Unit, QuickEditorError>(Unit), result)
    }

    @Test
    fun `given token stored when avatar upload fails then Failure result`() = runTest {
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val uri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.uploadCatching(any(), any()) } returns Result.Failure(ErrorType.SERVER)

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(Result.Failure<Unit, QuickEditorError>(QuickEditorError.Request(ErrorType.SERVER)), result)
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
