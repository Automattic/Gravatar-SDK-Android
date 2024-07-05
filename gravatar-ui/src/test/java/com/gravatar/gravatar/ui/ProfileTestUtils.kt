package com.gravatar.gravatar.ui

import com.gravatar.extensions.defaultProfile
import com.gravatar.extensions.emptyProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import java.net.URI
import com.gravatar.api.models.VerifiedAccount as LegacyVerifiedAccount

@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    level = DeprecationLevel.WARNING,
)
internal val completeProfile = emptyProfile(
    hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
    displayName = "Dominique Doe",
    jobTitle = "Farmer",
    company = "Farmers United",
    location = "Crac'h, France",
    pronouns = "They/Them",
    verifiedAccounts = listOf(
        LegacyVerifiedAccount(
            serviceType = "mastodon",
            serviceLabel = "Mastodon",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        LegacyVerifiedAccount(
            serviceType = "tumblr",
            serviceLabel = "Tumblr",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        LegacyVerifiedAccount(
            serviceType = "wordpress",
            serviceLabel = "WordPress",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
        LegacyVerifiedAccount(
            serviceType = "github",
            serviceLabel = "GitHub",
            url = URI("https://example.com"),
            serviceIcon = URI("https://example.com/icon.svg"),
        ),
    ),
    description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
        "doctor away. This about me description is quite long, this is good for testing.",
)

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
        },
        VerifiedAccount {
            serviceType = "tumblr"
            serviceLabel = "Tumblr"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
        },
        VerifiedAccount {
            serviceType = "wordpress"
            serviceLabel = "WordPress"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
        },
        VerifiedAccount {
            serviceType = "github"
            serviceLabel = "GitHub"
            url = URI("https://example.com")
            serviceIcon = URI("https://example.com/icon.svg")
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
