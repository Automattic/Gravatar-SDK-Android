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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

    @Test
    fun `given an app identifier then the app is marked as explicitly identified`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.hasExplicitAppIdentifier = true }
    }

    @Test
    fun `given an explicit app identifier when context is invoked then the identifier is kept`() {
        every { containerRule.gravatarSdkContainerMock.hasExplicitAppIdentifier } returns true
        val context = givenContext(appName = APP_NAME, versionName = VERSION_NAME)

        Gravatar.context(context)

        coVerify(exactly = 0) { containerRule.gravatarSdkContainerMock.appName = any() }
        coVerify(exactly = 0) { containerRule.gravatarSdkContainerMock.appVersion = any() }
    }

    @Test
    fun `given no explicit app identifier when context is invoked then the package values are used`() {
        every { containerRule.gravatarSdkContainerMock.hasExplicitAppIdentifier } returns false
        val context = givenContext(appName = APP_NAME, versionName = VERSION_NAME)

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appName = APP_NAME }
        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = VERSION_NAME }
    }

    @Test
    fun `given a version containing whitespace then it is accepted as-is`() {
        Gravatar.appIdentifier("wp-android", "27.6 (hotfix)")

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = "27.6 (hotfix)" }
    }

    @Test
    fun `given a version that cannot be sent as a header then the app identifier is rejected`() {
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("wp-android", "1.0-β") }
    }

    @Test
    fun `given a version containing a newline then the app identifier is rejected`() {
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("wp-android", "1.0\r\nX-Evil: y") }
    }

    @Test
    fun `given a name that cannot be sent as a header then the app identifier is rejected`() {
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("グラバタ", "1.0") }
    }

    @Test
    fun `given a name that is not a product token then the app identifier is rejected`() {
        // A space ends the product, so this would reach the backend as `My`.
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("My App", "1.0") }
        // A slash makes everything after it read as the version.
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("A/B Tester", "1.0") }
        // Legal in a header, but not in a token, and `Cafe` would only be a guess at the intent.
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("Café", "1.0") }
    }

    @Test
    fun `given a name that is not a product token then the message suggests one`() {
        val error = assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("My App") }

        assertTrue(error.message.orEmpty().contains("\"My-App\""), "was: ${error.message}")
    }

    @Test
    fun `given a product token name then it is stored as passed`() {
        Gravatar.appIdentifier("wp-android", "27.6")

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appName = "wp-android" }
    }

    @Test
    fun `given a blank name then the app identifier is rejected`() {
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("   ", "1.0") }
    }

    @Test
    fun `given a rejected app identifier then nothing is stored`() {
        assertFailsWith<IllegalArgumentException> { Gravatar.appIdentifier("wp-android", "1.0-β") }

        coVerify(exactly = 0) { containerRule.gravatarSdkContainerMock.appName = any() }
        coVerify(exactly = 0) { containerRule.gravatarSdkContainerMock.appVersion = any() }
    }

    @Test
    fun `given a manifest version that cannot be sent as a header then it is ignored`() {
        val context = givenContextReporting(appName = APP_NAME, versionName = "1.0-β")

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = null }
    }

    @Test
    fun `given a manifest version containing whitespace then it is kept`() {
        val context = givenContextReporting(appName = APP_NAME, versionName = "27.6 (hotfix)")

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = "27.6 (hotfix)" }
    }

    @Test
    fun `given a package info without a version name then the app version is null`() {
        val context = givenContextReporting(appName = APP_NAME, versionName = null)

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appVersion = null }
    }

    /** A context whose package manager reports [appName], and [versionName] unless it is null. */
    private fun givenContext(appName: String, versionName: String?): Context {
        val (context, packageManager) = givenContextWithAppName(appName)

        if (versionName == null) {
            every { packageManager.getPackageInfo(PACKAGE_NAME, 0) } throws PackageManager.NameNotFoundException()
        } else {
            every { packageManager.getPackageInfo(PACKAGE_NAME, 0) } returns packageInfo(versionName)
        }
        return context
    }

    /**
     * A context whose package manager reports [appName] and a real [PackageInfo] holding
     * [versionName] — including a null one, which is what an app with no `android:versionName`
     * produces. [givenContext] cannot express that, since it reads null as "the package is gone".
     */
    private fun givenContextReporting(appName: String, versionName: String?): Context {
        val (context, packageManager) = givenContextWithAppName(appName)
        every { packageManager.getPackageInfo(PACKAGE_NAME, 0) } returns packageInfo(versionName)
        return context
    }

    /**
     * The package manager is handed back alongside the context because stubbing it through the
     * chain (`every { context.packageManager.getPackageInfo(...) }`) would replace it with a fresh
     * child mock and discard the label stubs set here.
     */
    private fun givenContextWithAppName(appName: String): Pair<Context, PackageManager> {
        val context = mockk<Context>()
        val packageManager = mockk<PackageManager>()
        val applicationInfo = mockk<ApplicationInfo>()

        every { context.packageManager } returns packageManager
        every { context.packageName } returns PACKAGE_NAME
        every {
            packageManager.getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA)
        } returns applicationInfo
        every { packageManager.getApplicationLabel(applicationInfo) } returns appName
        return context to packageManager
    }

    private fun packageInfo(versionName: String?): PackageInfo = mockk<PackageInfo>(relaxed = true).apply {
        this.versionName = versionName
    }

    private companion object {
        const val APP_NAME = "Gravatar APP"
        const val PACKAGE_NAME = "com.gravatar"
        const val VERSION_NAME = "1.2.3"
    }
}
