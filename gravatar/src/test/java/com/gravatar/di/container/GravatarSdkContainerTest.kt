package com.gravatar.di.container

import com.gravatar.BuildConfig
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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
    fun `given a name holding characters a token cannot then they are replaced`() {
        container.appName = "A/B Tester"
        container.appVersion = "2.0"

        // Not `A/B-Tester/2.0`, which would parse as the product `A`.
        assertEquals("A-B-Tester/2.0 GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given an accented name then the letters survive without the accents`() {
        container.appName = "Café"

        assertEquals("Cafe GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given a name with nothing a token can hold then the user agent is just the sdk token`() {
        container.appName = "グラバタ"
        container.appVersion = "27.6"

        assertEquals("GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given a blank name then the user agent is just the sdk token`() {
        container.appName = "   "
        container.appVersion = "27.6"

        // Not `/27.6 GravatarAndroid/...`, which has no app name before the slash.
        assertEquals("GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given an app name then the token sent as the source header matches the user agent`() {
        container.appName = "Jetpack (Debug)"
        container.appVersion = "27.6"

        assertEquals("Jetpack-Debug", container.appNameToken)
        assertEquals("Jetpack-Debug/27.6 GravatarAndroid/${BuildConfig.SDK_VERSION}", container.userAgent)
    }

    @Test
    fun `given a name no token can represent then there is no token to send`() {
        container.appName = "グラバタ"

        assertNull(container.appNameToken)
    }

    @Test
    fun `given labels a product token can represent then they are reduced to one`() {
        assertEquals("wp-android", GravatarSdkContainer.toProductToken("wp-android"))
        assertEquals("Gravatar-Demo-App", GravatarSdkContainer.toProductToken("Gravatar Demo App"))
        assertEquals("Jetpack-Debug", GravatarSdkContainer.toProductToken("  Jetpack  Debug "))
        assertEquals("Jetpack-Debug", GravatarSdkContainer.toProductToken("Jetpack Debug"))
        assertEquals("A-B-Tester", GravatarSdkContainer.toProductToken("A/B Tester"))
        assertEquals("Cafe", GravatarSdkContainer.toProductToken("Café"))
        assertEquals("Jetpack-Debug", GravatarSdkContainer.toProductToken("Jetpack (Debug);"))
    }

    @Test
    fun `given labels a product token cannot represent then there is no token`() {
        assertNull(GravatarSdkContainer.toProductToken("グラバタ"))
        assertNull(GravatarSdkContainer.toProductToken(""))
        assertNull(GravatarSdkContainer.toProductToken("   "))
        assertNull(GravatarSdkContainer.toProductToken("///"))
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
