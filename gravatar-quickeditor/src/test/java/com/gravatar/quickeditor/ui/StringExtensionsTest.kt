package com.gravatar.quickeditor.ui

import org.junit.Assert.assertEquals
import org.junit.Test

internal class StringExtensionsTest {
    @Test
    fun `give a string with spaces when orphans removed then last space replaced with non-breaking`() {
        // Given
        val input = "This is a test string with spaces"
        val expected = "This is a test string with\u00A0spaces"

        // When
        val actual = input.removeOrphans

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `give a string without spaces when orphans removed then string remains unchanged`() {
        // Given
        val input = "ThisIsATestStringWithoutSpaces"

        // When
        val actual = input.removeOrphans

        // Then
        assertEquals(input, actual)
    }

    @Test
    fun `given an empty string when orphans removed then string remains unchanged`() {
        // Given
        val input = ""

        // When
        val actual = input.removeOrphans

        // Then
        assertEquals(input, actual)
    }
}
