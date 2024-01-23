package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class GravatarSdkContainerRule : TestRule {
    var gravatarSdkContainerMock = mockk<GravatarSdkContainer>()
    var gravatarApiServiceMock = mockk<GravatarApiService>()

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement {
        return object : Statement() {
            override fun evaluate() {
                gravatarSdkContainerMock = mockk<GravatarSdkContainer>()
                gravatarApiServiceMock = mockk<GravatarApiService>(relaxed = true)
                mockkObject(GravatarSdkContainer)
                every { GravatarSdkContainer.instance } returns gravatarSdkContainerMock
                every { gravatarSdkContainerMock.getGravatarApiService(any()) } returns gravatarApiServiceMock

                base.evaluate()
            }
        }
    }
}
