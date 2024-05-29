package com.gravatar

import io.mockk.coVerify
import org.junit.Rule
import org.junit.Test

class GravatarTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    @Test
    fun `given an api key when initialize method is invoked with it then it should be set internally`() {
        val apiKey = "API_KEY"

        Gravatar.initialize(apiKey)

        coVerify(exactly = 1) { containerRule.gravatarSdkContainerMock.apiKey = apiKey }
    }
}
