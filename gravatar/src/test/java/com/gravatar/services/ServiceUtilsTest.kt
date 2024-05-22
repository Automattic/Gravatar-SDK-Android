package com.gravatar.services

import com.gravatar.GravatarSdkContainerRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test

class ServiceUtilsTest {
    @get:Rule
    var containerRule = GravatarSdkContainerRule()

    @Test
    fun `given an api key when the sdk has been initialized then authenticationBearer should return a Bearer token`() {
        val apiKey = "API_KEY"

        containerRule.withApiKey(apiKey)

        assertEquals("Bearer $apiKey", authenticationBearer)
    }

    @Test
    fun `given no api key when getting authenticationBearer then it should be null`() {
        containerRule.withApiKey(null)

        assertNull(authenticationBearer)
    }
}
