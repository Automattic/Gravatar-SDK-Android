package com.gravatar

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(RobolectricTestRunner::class)
class GravatarApiTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private lateinit var gravatarApi: GravatarApi

    @Before
    fun setUp() {
        gravatarApi = GravatarApi()
    }

    @Test
    fun `given a file, email and accessToken when uploading avatar then Gravatar service is invoked`() = runTest {
        val uploadGravatarListener = spyk<GravatarApi.GravatarListener<Unit>>()
        val callResponse = mockk<Call<ResponseBody>>()
        every { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns callResponse
        every { callResponse.enqueue(any()) } answers { call ->
            @Suppress("UNCHECKED_CAST")
            (call.invocation.args[0] as? Callback<ResponseBody>)?.onResponse(
                callResponse,
                mockk(relaxed = true) {
                    every { isSuccessful } returns true
                },
            )
        }

        gravatarApi.uploadGravatar(File("avatarFile"), "email", "accessToken", uploadGravatarListener)

        verify(exactly = 1) {
            containerRule.gravatarApiServiceMock.uploadImage(
                "Bearer accessToken",
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
        verify(exactly = 1) {
            uploadGravatarListener.onSuccess(Unit)
        }
    }

    @Test
    fun `given gravatar update when a unknown error occurs then Gravatar returns UNKNOWN error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            100,
            GravatarApi.ErrorType.UNKNOWN,
        )

    @Test
    fun `given gravatar update when a server error occurs then Gravatar returns SERVER error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            HttpResponseCode.SERVER_ERRORS.random(),
            GravatarApi.ErrorType.SERVER,
        )

    @Test
    fun `given gravatar update when a timeout occurs then Gravatar returns TIMEOUT error`() =
        `given a gravatar update when a error occurs then Gravatar returns the expected error`(
            HttpResponseCode.HTTP_CLIENT_TIMEOUT,
            GravatarApi.ErrorType.TIMEOUT,
        )

    @Test
    fun `given gravatar update when a SocketTimeoutException occurs then Gravatar returns TIMEOUT error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            SocketTimeoutException(),
            GravatarApi.ErrorType.TIMEOUT,
        )

    @Test
    fun `given gravatar update when a UnknownHostException occurs then Gravatar returns NETWORK error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            UnknownHostException(),
            GravatarApi.ErrorType.NETWORK,
        )

    @Test
    fun `given gravatar update when a Exception occurs then Gravatar returns UNKNOWN error`() =
        `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
            Exception(),
            GravatarApi.ErrorType.UNKNOWN,
        )

    @Suppress("Cast")
    private fun `given a gravatar update when a error occurs then Gravatar returns the expected error`(
        httpResponseCode: Int,
        expectedErrorType: GravatarApi.ErrorType,
    ) = runTest {
        val uploadGravatarListener = spyk<GravatarApi.GravatarListener<Unit>>()
        val callResponse = mockk<Call<ResponseBody>>()
        every { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns callResponse
        every { callResponse.enqueue(any()) } answers { call ->
            @Suppress("UNCHECKED_CAST")
            (call.invocation.args[0] as? Callback<ResponseBody>)?.onResponse(
                callResponse,
                mockk(relaxed = true) {
                    every { isSuccessful } returns false
                    every { code() } returns httpResponseCode
                },
            )
        }

        gravatarApi.uploadGravatar(File("avatarFile"), "email", "accessToken", uploadGravatarListener)

        verify(exactly = 1) {
            uploadGravatarListener.onError(expectedErrorType)
        }
    }

    private fun `given a gravatar update when a exception occurs then Gravatar returns the expected error`(
        exception: Throwable,
        expectedErrorType: GravatarApi.ErrorType,
    ) = runTest {
        val uploadGravatarListener = spyk<GravatarApi.GravatarListener<Unit>>()
        val callResponse = mockk<Call<ResponseBody>>()
        every { containerRule.gravatarApiServiceMock.uploadImage(any(), any(), any()) } returns callResponse
        every { callResponse.enqueue(any()) } answers { call ->
            @Suppress("UNCHECKED_CAST")
            (call.invocation.args[0] as? Callback<ResponseBody>)?.onFailure(
                callResponse,
                exception,
            )
        }

        gravatarApi.uploadGravatar(File("avatarFile"), "email", "accessToken", uploadGravatarListener)

        verify(exactly = 1) {
            uploadGravatarListener.onError(expectedErrorType)
        }
    }
}
