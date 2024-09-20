package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer
import com.gravatar.logger.Logger
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
    internal var gravatarApiMock = mockk<GravatarApi>()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                gravatarSdkContainerMock = mockk<GravatarSdkContainer>(relaxed = true)
                mockkObject(GravatarSdkContainer)
                every { gravatarSdkContainerMock.dispatcherMain } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherDefault } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherIO } returns testDispatcher
                every { gravatarSdkContainerMock.apiKey } returns null
                every { GravatarSdkContainer.instance } returns gravatarSdkContainerMock
                every { gravatarSdkContainerMock.getGravatarV3Service(any()) } returns gravatarApiMock
                every { gravatarSdkContainerMock.getGravatarV3Service(any(), any()) } returns gravatarApiMock

                mockkObject(Logger)
                every { Logger.i(any(), any()) } returns 1
                every { Logger.w(any(), any()) } returns 1
                every { Logger.e(any(), any()) } returns 1

                base.evaluate()
            }
        }
    }

    fun withApiKey(apiKey: String? = DEFAULT_API_KEY) {
        every { gravatarSdkContainerMock.apiKey } returns apiKey
    }
}
