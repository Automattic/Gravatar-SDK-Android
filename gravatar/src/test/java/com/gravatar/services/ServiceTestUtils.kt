package com.gravatar.services

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.assertFailsWith

internal fun runTestExpectingGravatarException(
    errorType: ErrorType,
    originalException: Class<out Exception>,
    block: suspend () -> Unit,
) {
    val exception = assertFailsWith<GravatarException> {
        runTest {
            block()
        }
    }
    assertEquals(errorType, exception.errorType)
    assertTrue(originalException.isInstance(exception.originalException))
}
