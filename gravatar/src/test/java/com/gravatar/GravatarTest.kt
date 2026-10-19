package com.gravatar

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class GravatarTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    @Test
    fun `given an api key when initialize method is invoked with it then it should be set internally`() {
        val apiKey = "API_KEY"

        Gravatar.apiKey(apiKey)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.apiKey = apiKey }
    }

    @Test
    fun `given a context when initialize method is invoked then the app name is extracted`() {
        val context = givenContext(appName = APP_NAME, versionName = VERSION_NAME)

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appName = APP_NAME }
    }

    @Test
    fun `given a context when initialize method is invoked then the app version is extracted`() {
        val context = givenContext(appName = APP_NAME, versionName = VERSION_NAME)

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = VERSION_NAME }
    }

    @Test
    fun `given a package manager that cannot find the package then the app version is null`() {
        val context = givenContext(appName = APP_NAME, versionName = null)

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = null }
    }

    @Test
    fun `given an app identifier when it is set then both name and version are stored`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appName = "wp-android" }
        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = "27.6" }
    }

    @Test
    fun `given an app identifier without a version then the version is cleared`() {
        Gravatar.appIdentifier("wp-android")

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = null }
    }

    /** A context whose package manager reports [appName], and [versionName] unless it is null. */
    private fun givenContext(appName: String, versionName: String?): Context {
        val context = mockk<Context>()
        val packageManager = mockk<PackageManager>()
        val applicationInfo = mockk<ApplicationInfo>()

        every { context.packageManager } returns packageManager
        every { context.packageName } returns PACKAGE_NAME
        every {
            packageManager.getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA)
        } returns applicationInfo
        every { packageManager.getApplicationLabel(applicationInfo) } returns appName

        if (versionName == null) {
            every { packageManager.getPackageInfo(PACKAGE_NAME, 0) } throws PackageManager.NameNotFoundException()
        } else {
            val packageInfo = mockk<PackageInfo>(relaxed = true)
            packageInfo.versionName = versionName
            every { packageManager.getPackageInfo(PACKAGE_NAME, 0) } returns packageInfo
        }
        return context
    }

    private companion object {
        const val APP_NAME = "Gravatar APP"
        const val PACKAGE_NAME = "com.gravatar"
        const val VERSION_NAME = "1.2.3"
    }
}
