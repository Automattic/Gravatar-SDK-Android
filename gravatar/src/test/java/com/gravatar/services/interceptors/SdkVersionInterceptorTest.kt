package com.gravatar.services.interceptors

import com.gravatar.BuildConfig
import com.gravatar.GravatarSdkContainerRule
import com.gravatar.di.container.GravatarSdkContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkObject
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
        every { containerRule.gravatarSdkContainerMock.appNameToken } returns "Gravatar-APP"

        val sent = intercept(Request.Builder().url(URL).build())

        assertEquals("Android", sent.header("X-Platform"))
        assertEquals(BuildConfig.SDK_VERSION, sent.header("X-SDK-Version"))
        assertEquals("Gravatar-APP", sent.header("X-Source"))
    }

    @Test
    fun `given no app name when intercepting then no source header is sent`() {
        every { containerRule.gravatarSdkContainerMock.appNameToken } returns null

        val sent = intercept(Request.Builder().url(URL).build())

        assertNull(sent.header("X-Source"))
    }

    /**
     * The real container rather than the rule's mock, because the point is that the two headers
     * agree: `X-Source` used to carry the raw launcher label while the User-Agent carried a token
     * made of it, so one request could name the app two different ways.
     */
    @Test
    fun `given a label a token cannot hold verbatim then both headers name the app the same way`() {
        withRealContainer(appName = "Jetpack (Debug)", appVersion = "27.6") {
            val sent = intercept(Request.Builder().url(URL).build())

            assertEquals("Jetpack-Debug", sent.header("X-Source"))
            assertEquals("Jetpack-Debug/27.6 GravatarAndroid/${BuildConfig.SDK_VERSION}", sent.header("User-Agent"))
        }
    }

    /**
     * The regression this guards against was a launcher label reaching OkHttp unchecked: a
     * non-ASCII one made `Headers.checkValue` throw from inside the interceptor, failing every
     * request the SDK made.
     */
    @Test
    fun `given a non-ascii app name then intercepting does not throw and sends no source header`() {
        withRealContainer(appName = "グラバタ", appVersion = "27.6") {
            val sent = intercept(Request.Builder().url(URL).build())

            assertNull(sent.header("X-Source"))
            assertEquals("GravatarAndroid/${BuildConfig.SDK_VERSION}", sent.header("User-Agent"))
        }
    }

    @Test
    fun `given a blank app name then no source header is sent`() {
        withRealContainer(appName = "   ", appVersion = "27.6") {
            val sent = intercept(Request.Builder().url(URL).build())

            assertNull(sent.header("X-Source"))
            assertEquals("GravatarAndroid/${BuildConfig.SDK_VERSION}", sent.header("User-Agent"))
        }
    }

    /** Runs [block] against the real singleton, since the rule installs a mock for every test. */
    private fun withRealContainer(appName: String, appVersion: String?, block: () -> Unit) {
        unmockkObject(GravatarSdkContainer)
        GravatarSdkContainer.instance.appName = appName
        GravatarSdkContainer.instance.appVersion = appVersion
        try {
            block()
        } finally {
            GravatarSdkContainer.instance.appName = null
            GravatarSdkContainer.instance.appVersion = null
        }
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
