package com.gravatar.quickeditor.data

import android.content.Context
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class AcceptedLanguageInterceptorTest {
    private val mockWebServer = MockWebServer()

    private lateinit var client: OkHttpClient

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        client = OkHttpClient.Builder()
            .addInterceptor(AcceptedLanguageInterceptor(context))
            .build()
    }

    @After
    fun after() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given one languages when intercepting then Accept-Language header set`() {
        mockkStatic(Context::languagesList)
        every { context.languagesList } returns LocaleListCompat.create(Locale.ENGLISH)

        val mockResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        mockWebServer.start()

        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("en", recordedRequest.getHeader("Accept-Language"))
    }

    @Test
    fun `given many languages when intercepting then Accept-Language header set`() {
        mockkStatic(Context::languagesList)
        every { context.languagesList } returns LocaleListCompat.create(Locale.ENGLISH, Locale.FRENCH, Locale.GERMANY)

        val mockResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        mockWebServer.start()

        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("en,fr;q=0.9,de;q=0.8", recordedRequest.getHeader("Accept-Language"))
    }
}
