package com.gravatar.extensions

import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.ProfileUrl
import com.gravatar.api.models.GalleryImage
import com.gravatar.api.models.Link
import com.gravatar.api.models.Profile
import com.gravatar.api.models.ProfileContactInfo
import com.gravatar.api.models.ProfilePayments
import com.gravatar.api.models.VerifiedAccount
import com.gravatar.types.Hash
import java.net.URI
import java.time.Instant

/**
 * Get the hash for a user profile.
 */
public fun Profile.hash(): Hash {
    return Hash(this.hash)
}

/**
 * Get the avatar URL for a user profile.
 */
public fun Profile.avatarUrl(avatarQueryOptions: AvatarQueryOptions? = null): AvatarUrl {
    return AvatarUrl(this.hash(), avatarQueryOptions)
}

/**
 * Get the profile URL for a user profile.
 */
public fun Profile.profileUrl(): ProfileUrl {
    return ProfileUrl(this.hash())
}

/**
 * Get formatted user info for a user profile.
 */
public fun Profile.formattedUserInfo(): String {
    val line1 = listOf(this.jobTitle, this.company).filter { it.isNotBlank() }.joinToString(", ")
    val line2 = listOf(this.pronunciation, this.pronouns, this.location).filter {
        it.isNotBlank()
    }.joinToString(" · ")
    return listOf(line1, line2).filter { it.isNotBlank() }.joinToString("\n")
}

/**
 * Instantiates an empty profile with the given hash by default.
 * You can also provide other values to override the default empty values.
 */
public fun emptyProfile(
    hash: String,
    displayName: String = "",
    profileUrl: URI = ProfileUrl(Hash(hash)).url.toURI(),
    avatarUrl: URI = AvatarUrl(Hash(hash)).url().toURI(),
    avatarAltText: String = "",
    location: String = "",
    description: String = "",
    jobTitle: String = "",
    company: String = "",
    verifiedAccounts: List<VerifiedAccount> = emptyList(),
    pronunciation: String = "",
    pronouns: String = "",
    links: List<Link>? = null,
    payments: ProfilePayments? = null,
    contactInfo: ProfileContactInfo? = null,
    gallery: List<GalleryImage>? = null,
    numberVerifiedAccounts: Int? = null,
    lastProfileEdit: Instant? = null,
    registrationDate: Instant? = null,
): Profile = Profile(
    hash = hash,
    displayName = displayName,
    profileUrl = profileUrl,
    avatarUrl = avatarUrl,
    avatarAltText = avatarAltText,
    location = location,
    description = description,
    jobTitle = jobTitle,
    company = company,
    verifiedAccounts = verifiedAccounts,
    pronunciation = pronunciation,
    pronouns = pronouns,
    links = links,
    payments = payments,
    contactInfo = contactInfo,
    gallery = gallery,
    numberVerifiedAccounts = numberVerifiedAccounts,
    lastProfileEdit = lastProfileEdit,
    registrationDate = registrationDate,
)
