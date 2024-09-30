package com.gravatar.quickeditor.data

import android.content.Context
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class AcceptedLanguageInterceptorTest {
    private val mockWebServer = MockWebServer()

    private lateinit var api: TestApi

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val testResponse = "\"name\""

    @Before
    fun setUp() {
        val client = OkHttpClient.Builder()
            .addInterceptor(AcceptedLanguageInterceptor(context))
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(TestApi::class.java)
    }

    @Test
    fun `given one languages when intercepting then Accept-Language header set`() {
        mockkStatic(Context::languagesList)
        every { context.languagesList } returns LocaleListCompat.create(Locale.ENGLISH)
        val successResponse = MockResponse().setBody(testResponse)
        mockWebServer.enqueue(successResponse)

        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader("Accept-Language")
        assertEquals("en", header)
    }

    @Test
    fun `given many languages when intercepting then Accept-Language header set`() {
        mockkStatic(Context::languagesList)
        every { context.languagesList } returns LocaleListCompat.create(Locale.ENGLISH, Locale.FRENCH, Locale.GERMANY)
        val successResponse = MockResponse().setBody(testResponse)
        mockWebServer.enqueue(successResponse)

        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader("Accept-Language")
        assertEquals("en,fr;q=0.9,de;q=0.8", header)
    }
}

private interface TestApi {
    @GET("/test")
    fun test(): Call<String>
}
