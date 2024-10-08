package com.gravatar

import android.content.Context
import android.content.pm.ApplicationInfo
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
        val appName = "Gravatar APP"
        val packageName = "com.gravatar"
        val context = mockk<Context>()
        val packageManager = mockk<PackageManager>()
        val applicationInfo = mockk<ApplicationInfo>()

        every { context.packageManager } returns packageManager
        every { context.packageName } returns packageName
        every { packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA) } returns applicationInfo
        every { packageManager.getApplicationLabel(applicationInfo) } returns appName

        Gravatar.context(context)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.appName = appName }
    }
}
