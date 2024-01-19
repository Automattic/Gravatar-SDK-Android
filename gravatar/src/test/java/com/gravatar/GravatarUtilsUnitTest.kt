package com.gravatar

import junit.framework.TestCase.assertEquals
import org.junit.Test

class GravatarUtilsUnitTest {
    @Test
    fun sha256Hash_isCorrect() {
        assertEquals(
            "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
            "Hello World!".sha256Hash(),
        )
    }

    @Test
    fun gravatarHashEmail_isCorrect() {
        assertEquals(
            "31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            emailAddressToGravatarHash("example@example.com"),
        )
    }

    @Test
    fun gravatarHashEmailShouldTrimInputPrefix() {
        assertEquals(
            emailAddressToGravatarHash("   example@example.com"),
            emailAddressToGravatarHash("example@example.com"),
        )
    }

    @Test
    fun gravatarHashEmailShouldTrimInputPostfix() {
        assertEquals(
            emailAddressToGravatarHash("example@example.com  "),
            emailAddressToGravatarHash("example@example.com"),
        )
    }

    @Test
    fun gravatarHashEmailShouldTrimInputs() {
        assertEquals(
            emailAddressToGravatarHash("    example@example.com   "),
            emailAddressToGravatarHash("example@example.com"),
        )
    }

    @Test
    fun gravatarHashEmailShouldLowerCaseInput() {
        assertEquals(
            emailAddressToGravatarHash("example@EXAMPLE.com"),
            emailAddressToGravatarHash("example@example.com"),
        )
    }

    @Test
    fun gravatarHashEmailShouldLowerCaseAndTrimInput() {
        assertEquals(
            emailAddressToGravatarHash(" EXample@EXAMPLE.com  "),
            emailAddressToGravatarHash("example@example.com"),
        )
    }
}
