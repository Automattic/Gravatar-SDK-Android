package com.gravatar.gravatar.ui

import android.content.Context
import coil.decode.SvgDecoder
import com.gravatar.Gravatar
import com.gravatar.ui.gravatarImageRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Image loads go through the host app's Coil `ImageLoader`, which the SDK's interceptors never
 * touch, so without this header they reach gravatar.com as `okhttp/<version>`.
 */
@RunWith(RobolectricTestRunner::class)
class GravatarImageRequestTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `given an app identifier then the request carries the sdk user agent`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        val request = gravatarImageRequest(context, IMAGE_URL)

        assertEquals("wp-android/27.6", request.headers["User-Agent"]?.substringBefore(" Gravatar"))
        assertEquals(Gravatar.userAgent, request.headers["User-Agent"])
    }

    @Test
    fun `given extra request options then they are applied alongside the user agent`() {
        val request = gravatarImageRequest(context, "https://gravatar.com/icon.svg") {
            decoderFactory(SvgDecoder.Factory())
        }

        assertEquals(Gravatar.userAgent, request.headers["User-Agent"])
        assertTrue(request.decoderFactory is SvgDecoder.Factory)
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
