package com.gravatar

import com.gravatar.extensions.emptyProfile
import com.gravatar.extensions.formattedUserInfo
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UserProfileExtensionsTest {
    @Test
    fun `given an empty user profile return an empty formatted info string`() {
        val userProfile = emptyProfile(hash = "")
        assertEquals("", userProfile.formattedUserInfo())
    }

    @Test
    fun `given a user profile with job title set return a correctly formatted info string`() {
        val userProfile = emptyProfile(hash = "", jobTitle = "Pony Trainer")
        assertEquals("Pony Trainer", userProfile.formattedUserInfo())
    }

    @Test
    fun `given a user profile with job title and company set return a correctly formatted info string`() {
        val userProfile = emptyProfile(hash = "", jobTitle = "Pony Trainer", company = "Pony Land")
        assertEquals("Pony Trainer, Pony Land", userProfile.formattedUserInfo())
    }

    @Test
    fun `given a user profile with location only return a correctly formatted info string`() {
        val userProfile = emptyProfile(hash = "", location = "Crac'h, France")
        assertEquals("Crac'h, France", userProfile.formattedUserInfo())
    }

    @Test
    fun `given a fully set user profile return a correctly formatted info string`() {
        val userProfile =
            emptyProfile(
                hash = "",
                jobTitle = "Pony Trainer",
                company = "Pony Land",
                location = "Crac'h, France",
                pronouns = "They/Them",
                pronunciation = "Tony but with a P",
            )
        assertEquals(
            "Pony Trainer, Pony Land\n" +
                "Tony but with a P · They/Them · Crac'h, France",
            userProfile.formattedUserInfo(),
        )
    }

    @Test
    fun `given a profile without pronouns return a correctly formatted info string`() {
        val userProfile =
            emptyProfile(
                hash = "",
                jobTitle = "Pony Trainer",
                company = "Pony Land",
                location = "Crac'h, France",
                pronunciation = "Tony but with a P",
            )
        assertEquals(
            "Pony Trainer, Pony Land\n" +
                "Tony but with a P · Crac'h, France",
            userProfile.formattedUserInfo(),
        )
    }
}
