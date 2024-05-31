package com.gravatar.gravatar.ui

import com.gravatar.api.models.VerifiedAccount
import com.gravatar.extensions.emptyProfile
import java.net.URI

internal val completeProfile = emptyProfile(
    hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
    displayName = "Dominique Doe",
    jobTitle = "Farmer",
    company = "Farmers United",
    location = "Crac'h, France",
    pronouns = "They/Them",
    verifiedAccounts = listOf(
        VerifiedAccount(
            serviceType = "mastodon",
            serviceLabel = "Mastodon",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        VerifiedAccount(
            serviceType = "tumblr",
            serviceLabel = "Tumblr",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        VerifiedAccount(
            serviceType = "wordpress",
            serviceLabel = "WordPress",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        VerifiedAccount(
            serviceType = "github",
            serviceLabel = "GitHub",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
    ),
    description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
        "doctor away. This about me description is quite long, this is good for testing.",
)
