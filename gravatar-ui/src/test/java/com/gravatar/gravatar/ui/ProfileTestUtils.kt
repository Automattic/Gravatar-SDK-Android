package com.gravatar.gravatar.ui

import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import java.net.URI

internal fun completeProfile(
    hash: String = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
    displayName: String = "Dominique Doe",
    jobTitle: String = "Farmer",
    company: String = "Farmers United",
    location: String = "Crac'h, France",
    pronouns: String = "They/Them",
    verifiedAccounts: List<VerifiedAccount> = listOf(
        VerifiedAccount {
            serviceType = "mastodon"
            serviceLabel = "Mastodon"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
            isHidden = false
        },
        VerifiedAccount {
            serviceType = "twitch"
            serviceLabel = "Twitch"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
            isHidden = true
        },
        VerifiedAccount {
            serviceType = "tumblr"
            serviceLabel = "Tumblr"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
            isHidden = false
        },
        VerifiedAccount {
            serviceType = "wordpress"
            serviceLabel = "WordPress"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
            isHidden = false
        },
        VerifiedAccount {
            serviceType = "github"
            serviceLabel = "GitHub"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
            isHidden = false
        },
    ),
    description: String = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
        "doctor away. This about me description is quite long, this is good for testing.",
): Profile = defaultProfile(
    hash = hash,
    displayName = displayName,
    jobTitle = jobTitle,
    company = company,
    location = location,
    pronouns = pronouns,
    verifiedAccounts = verifiedAccounts,
    description = description,
)
