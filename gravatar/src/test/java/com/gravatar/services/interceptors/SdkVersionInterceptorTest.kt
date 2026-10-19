package com.gravatar.services.interceptors

import com.gravatar.BuildConfig
import com.gravatar.GravatarSdkContainerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SdkVersionInterceptorTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    private val interceptor = SdkVersionInterceptor()

    @Test
    fun `given an app identifier when intercepting then the User-Agent carries it`() {
        every { containerRule.gravatarSdkContainerMock.userAgent } returns "wp-android/27.6 GravatarAndroid/2.5.0"

        val sent = intercept(Request.Builder().url(URL).build())

        assertEquals("wp-android/27.6 GravatarAndroid/2.5.0", sent.header("User-Agent"))
    }

    @Test
    fun `given a request that already has a User-Agent when intercepting then it is left alone`() {
        every { containerRule.gravatarSdkContainerMock.userAgent } returns "GravatarAndroid/2.5.0"

        val sent = intercept(
            Request.Builder().url(URL).header("User-Agent", "caller/1.0").build(),
        )

        assertEquals("caller/1.0", sent.header("User-Agent"))
    }

    @Test
    fun `given an app name when intercepting then the platform and source headers are set`() {
        every { containerRule.gravatarSdkContainerMock.appName } returns "Gravatar APP"

        val sent = intercept(Request.Builder().url(URL).build())

        assertEquals("Android", sent.header("X-Platform"))
        assertEquals(BuildConfig.SDK_VERSION, sent.header("X-SDK-Version"))
        assertEquals("Gravatar APP", sent.header("X-Source"))
    }

    @Test
    fun `given no app name when intercepting then no source header is sent`() {
        every { containerRule.gravatarSdkContainerMock.appName } returns null

        val sent = intercept(Request.Builder().url(URL).build())

        assertNull(sent.header("X-Source"))
    }

    /** Runs the interceptor over [request] and returns the request it actually handed downstream. */
    private fun intercept(request: Request): Request {
        val chain = mockk<Interceptor.Chain>()
        val forwarded = slot<Request>()
        every { chain.request() } returns request
        every { chain.proceed(capture(forwarded)) } answers {
            Response.Builder()
                .request(forwarded.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }

        interceptor.intercept(chain)

        return forwarded.captured
    }

    private companion object {
        const val URL = "https://api.gravatar.com/v3/me/profile"
    }
}
