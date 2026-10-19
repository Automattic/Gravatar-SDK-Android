package com.gravatar.di.container

import com.gravatar.BuildConfig
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Exercises the real container rather than the mock the other suites install, since the point here
 * is how [GravatarSdkContainer.userAgent] is composed. The singleton is shared, so every field this
 * touches is reset afterwards.
 */
class GravatarSdkContainerTest {
    private val container get() = GravatarSdkContainer.instance

    @Before
    fun setUp() {
        // Another suite may have left the object mocked; these tests need the real implementation.
        unmockkObject(GravatarSdkContainer)
        container.appName = null
        container.appVersion = null
    }

    @After
    fun tearDown() {
        container.appName = null
        container.appVersion = null
    }

    @Test
    fun `given no app identifier then the user agent is just the sdk token`() {
        assertEquals("GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given an app name and version then both lead the user agent`() {
        container.appName = "wp-android"
        container.appVersion = "27.6"

        assertEquals("wp-android/27.6 GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given an app name without a version then the version is omitted`() {
        container.appName = "wp-android"

        assertEquals("wp-android GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given an app name with whitespace then it is replaced so the token stays valid`() {
        container.appName = "  Jetpack  Debug "
        container.appVersion = "27.6"

        assertEquals("Jetpack-Debug/27.6 GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given a version with whitespace then it is kept, unlike the name`() {
        container.appName = "wp-android"
        container.appVersion = "27.6 (hotfix)"

        assertEquals("wp-android/27.6 (hotfix) GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given values a header can carry then they are header-safe`() {
        assertTrue(GravatarSdkContainer.isHeaderSafe("wp-android"))
        assertTrue(GravatarSdkContainer.isHeaderSafe("27.6 (hotfix)"), "space is legal in a header value")
        assertTrue(GravatarSdkContainer.isHeaderSafe("a\tb"), "tab is legal in a header value")
        assertTrue(GravatarSdkContainer.isHeaderSafe("!~"), "the printable ASCII bounds")
        assertTrue(GravatarSdkContainer.isHeaderSafe(""))
    }

    @Test
    fun `given values a header cannot carry then they are not header-safe`() {
        assertFalse(GravatarSdkContainer.isHeaderSafe("1.0-β"), "non-ASCII")
        assertFalse(GravatarSdkContainer.isHeaderSafe("Jetpack Debug"), "non-breaking space")
        assertFalse(GravatarSdkContainer.isHeaderSafe("1.0\r\nX-Evil: y"), "CRLF")
        assertFalse(GravatarSdkContainer.isHeaderSafe("a\u007fb"), "DEL is above the printable range")
        assertFalse(GravatarSdkContainer.isHeaderSafe("a\u0000b"), "NUL is below the printable range")
    }
}
