package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer
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

    internal var gravatarSdkContainerMock = mockk<GravatarSdkContainer>()
    internal var gravatarApiServiceMock = mockk<GravatarApiService>()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                gravatarSdkContainerMock = mockk<GravatarSdkContainer>()
                gravatarApiServiceMock = mockk<GravatarApiService>(relaxed = true)
                mockkObject(GravatarSdkContainer)
                every { gravatarSdkContainerMock.dispatcherMain } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherDefault } returns testDispatcher
                every { gravatarSdkContainerMock.dispatcherIO } returns testDispatcher
                every { GravatarSdkContainer.instance } returns gravatarSdkContainerMock
                every { gravatarSdkContainerMock.getGravatarApiV1Service(any()) } returns gravatarApiServiceMock
                every { gravatarSdkContainerMock.getGravatarApiV3Service(any()) } returns gravatarApiServiceMock

                base.evaluate()
            }
        }
    }
}
