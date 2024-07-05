package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer
import com.gravatar.services.GravatarApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
class GravatarSdkContainerRule : TestRule {
    val testDispatcher = UnconfinedTestDispatcher()

    companion object {
        const val DEFAULT_API_KEY = "apiKey"
    }

    internal var gravatarSdkContainerMock = mockk<GravatarSdkContainer>()
    internal var gravatarApiServiceMock = mockk<GravatarApiService>()
    internal var gravatarApiMock = mockk<GravatarApi>()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                gravatarSdkContainerMock = mockk<GravatarSdkContainer>(relaxed = true)
                gravatarApiServiceMock = mockk<GravatarApiService>(relaxed = true)
                mockkObject(GravatarSdkContainer)
                every { gravatarSdkContainerMock.dispatcherMain } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherDefault } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherIO } returns testDispatcher
                every { gravatarSdkContainerMock.apiKey } returns null
                every { GravatarSdkContainer.instance } returns gravatarSdkContainerMock
                every { gravatarSdkContainerMock.getGravatarV1Service(any()) } returns gravatarApiMock
                every { gravatarSdkContainerMock.getGravatarApiV3Service(any()) } returns gravatarApiServiceMock
                every { gravatarSdkContainerMock.getGravatarV3Service(any()) } returns gravatarApiMock

                base.evaluate()
            }
        }
    }

    fun withApiKey(apiKey: String? = DEFAULT_API_KEY) {
        every { gravatarSdkContainerMock.apiKey } returns apiKey
    }
}
