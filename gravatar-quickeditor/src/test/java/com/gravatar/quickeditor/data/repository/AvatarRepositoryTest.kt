package com.gravatar.quickeditor.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.storage.DataStoreTokenStorage
import com.gravatar.quickeditor.ui.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
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
import java.net.URI

class AvatarRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule(testDispatcher)

    private val avatarService = mockk<AvatarService>()
    private val tokenStorage = mockk<DataStoreTokenStorage>()

    private lateinit var avatarRepository: AvatarRepository

    private val email = Email("email")

    @Before
    fun setUp() {
        avatarRepository = AvatarRepository(
            avatarService = avatarService,
            tokenStorage = tokenStorage,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `given email when token not found then TokenNotFound result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = avatarRepository.getAvatars(email)

        assertEquals(GravatarResult.Failure<EmailAvatars, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when get avatars fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"

        coEvery { avatarService.retrieveCatching(any(), any()) } returns GravatarResult.Failure(ErrorType.Server)

        val result = avatarRepository.getAvatars(email)

        assertEquals(
            GravatarResult.Failure<EmailAvatars, QuickEditorError>(QuickEditorError.Request(ErrorType.Server)),
            result,
        )
    }

    @Test
    fun `given token stored when get avatars succeed then Success result`() = runTest {
        val imageId = "2"
        val avatar = createAvatar(imageId, isSelected = true)
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.retrieveCatching(any(), any()) } returns GravatarResult.Success(listOf(avatar))

        val result = avatarRepository.getAvatars(email)

        assertEquals(
            GravatarResult.Success<EmailAvatars, QuickEditorError>(EmailAvatars(listOf(avatar), imageId)),
            result,
        )
    }

    @Test
    fun `given email stored when token not found then TokenNotFound result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns null

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(GravatarResult.Failure<String, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when avatar selected fails then Failure result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery {
            avatarService.setAvatarCatching(any(), any(), any())
        } returns GravatarResult.Failure(ErrorType.Unknown)

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(
            GravatarResult.Failure<String, QuickEditorError>(QuickEditorError.Request(ErrorType.Unknown)),
            result,
        )
    }

    @Test
    fun `given token stored when avatar selected succeeds then Success result`() = runTest {
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.setAvatarCatching(any(), any(), any()) } returns GravatarResult.Success(Unit)

        val result = avatarRepository.selectAvatar(email, "avatarId")

        assertEquals(GravatarResult.Success<Unit, QuickEditorError>(Unit), result)
    }

    @Test
    fun `given token not stored when avatar upload then Failure result`() = runTest {
        val uri = mockk<Uri>()
        coEvery { tokenStorage.getToken(any()) } returns null
        coEvery { avatarService.uploadCatching(any(), any()) } returns GravatarResult.Success(createAvatar("1"))

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(GravatarResult.Failure<Unit, QuickEditorError>(QuickEditorError.TokenNotFound), result)
    }

    @Test
    fun `given token stored when avatar upload succeeds then Success result`() = runTest {
        val avatar = createAvatar("2")
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val uri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.uploadCatching(any(), any()) } returns GravatarResult.Success(avatar)

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(GravatarResult.Success<Avatar, QuickEditorError>(avatar), result)
    }

    @Test
    fun `given token stored when avatar upload fails then Failure result`() = runTest {
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val uri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery { tokenStorage.getToken(any()) } returns "token"
        coEvery { avatarService.uploadCatching(any(), any()) } returns GravatarResult.Failure(ErrorType.Server)

        val result = avatarRepository.uploadAvatar(email, uri)

        assertEquals(GravatarResult.Failure<Unit, QuickEditorError>(QuickEditorError.Request(ErrorType.Server)), result)
    }

    private fun createAvatar(id: String, isSelected: Boolean = false) = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test")
        imageId = id
        rating = Avatar.Rating.G
        altText = "alt"
        updatedDate = ""
        selected = isSelected
    }
}
