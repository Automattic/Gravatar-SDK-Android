package com.gravatar.quickeditor.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gravatar.Gravatar
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Image loads go through the host app's Coil `ImageLoader`, which the SDK's interceptors never
 * touch, so without this header they reach gravatar.com as `okhttp/<version>`.
 */
@RunWith(RobolectricTestRunner::class)
class GravatarImageRequestTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `given an app identifier then the request carries the sdk user agent`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        val request = gravatarImageRequest(context, IMAGE_URL)

        assertEquals(Gravatar.userAgent, request.headers["User-Agent"])
        assertEquals("wp-android/27.6", request.headers["User-Agent"]?.substringBefore(" Gravatar"))
    }

    @Test
    fun `given extra request options then they are applied alongside the user agent`() {
        val request = gravatarImageRequest(context, IMAGE_URL) { memoryCacheKey("avatar-key") }

        assertEquals(Gravatar.userAgent, request.headers["User-Agent"])
        assertEquals("avatar-key", request.memoryCacheKey?.key)
    }

    @Test
    fun `given data then the request loads it`() {
        val request = gravatarImageRequest(context, IMAGE_URL)

        assertEquals(IMAGE_URL, request.data)
    }

    private companion object {
        const val IMAGE_URL = "https://gravatar.com/avatar/hash"
    }
}
