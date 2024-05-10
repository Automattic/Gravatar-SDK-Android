package com.gravatar.gravatar.ui

import com.gravatar.api.models.Account
import com.gravatar.api.models.Email
import com.gravatar.api.models.UserProfile

internal val completeProfile = UserProfile(
    hash = "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
    displayName = "Dominique Doe",
    preferredUsername = "ddoe",
    jobTitle = "Farmer",
    company = "Farmers United",
    currentLocation = "Crac'h, France",
    pronouns = "They/Them",
    accounts = listOf(
        Account(name = "Mastodon", url = "https://mastodon.social/@ddoe", shortname = "mastodon"),
        Account(name = "Tumblr", url = "https://ddoe.tumblr.com", shortname = "tumblr"),
        Account(name = "WordPress", url = "https://ddoe.wordpress.com", shortname = "wordpress"),
    ),
    aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
        "doctor away. This about me description is quite long, this is good for testing.",
    emails = listOf(Email(primary = true, value = "john@doe.com")),
)
