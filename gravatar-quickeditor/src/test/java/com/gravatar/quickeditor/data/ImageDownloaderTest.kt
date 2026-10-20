package com.gravatar.quickeditor.data

import android.app.DownloadManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.test.core.app.ApplicationProvider
import com.gravatar.BuildConfig
import com.gravatar.Gravatar
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowDownloadManager
import java.net.URI

@RunWith(RobolectricTestRunner::class)
class ImageDownloaderTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    @Before
    fun setUp() {
        // The downloader refuses to enqueue anything unless this package is installed and enabled.
        shadowOf(context.packageManager).installPackage(
            PackageInfo().apply {
                packageName = DOWNLOADS_PROVIDER
                applicationInfo = ApplicationInfo().apply { packageName = DOWNLOADS_PROVIDER }
            },
        )
    }

    /**
     * `X-Source` used to come from the launcher label while the User-Agent came from the SDK, so a
     * download could name the app two different ways in the same request.
     */
    @Test
    fun `given an app identifier when downloading then both headers name the app the same way`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        val headers = headersOfDownload()

        assertEquals("wp-android", headers["X-Source"])
        assertEquals("wp-android/27.6 GravatarAndroid/${BuildConfig.SDK_VERSION}", headers["User-Agent"])
    }

    @Test
    fun `when downloading then the platform and sdk version headers are sent`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        val headers = headersOfDownload()

        assertEquals("Android", headers["X-Platform"])
        assertEquals(BuildConfig.SDK_VERSION, headers["X-SDK-Version"])
    }

    /** Enqueues a download and returns the headers the request was built with. */
    private fun headersOfDownload(): Map<String, String> {
        ImageDownloader(context).downloadImage(URI("https://gravatar.com/avatar/hash"))

        val shadowManager = shadowOf(downloadManager) as ShadowDownloadManager
        val request = shadowManager.getRequest(shadowManager.requestCount.toLong() - 1)
        return Shadow.extract<ShadowDownloadManager.ShadowRequest>(request)
            .requestHeaders
            .associate { it.first to it.second }
    }

    private companion object {
        const val DOWNLOADS_PROVIDER = "com.android.providers.downloads"
    }
}
