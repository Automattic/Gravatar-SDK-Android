package com.gravatar.extensions

import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.ProfileUrl
import com.gravatar.restapi.models.GalleryImage
import com.gravatar.restapi.models.Link
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import com.gravatar.restapi.models.ProfilePayments
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.types.Hash
import java.net.URI
import java.time.Instant
import com.gravatar.api.models.GalleryImage as LegacyGalleryImage
import com.gravatar.api.models.Link as LegacyLink
import com.gravatar.api.models.Profile as LegacyProfile
import com.gravatar.api.models.ProfileContactInfo as LegacyProfileContactInfo
import com.gravatar.api.models.ProfilePayments as LegacyProfilePayments
import com.gravatar.api.models.VerifiedAccount as LegacyVerifiedAccount

/**
 * Get the hash for a user profile.
 */
public fun LegacyProfile.hash(): Hash {
    return Hash(this.hash)
}

/**
 * Get the avatar URL for a user profile.
 */
public fun LegacyProfile.avatarUrl(avatarQueryOptions: AvatarQueryOptions? = null): AvatarUrl {
    return AvatarUrl(this.hash(), avatarQueryOptions)
}

/**
 * Get the profile URL for a user profile.
 */
public fun LegacyProfile.profileUrl(): ProfileUrl {
    return ProfileUrl(this.hash())
}

/**
 * Get formatted user info for a user profile.
 */
public fun LegacyProfile.formattedUserInfo(): String {
    val line1 = listOf(this.jobTitle, this.company).filter { it.isNotBlank() }.joinToString(", ")
    val line2 = listOf(this.pronunciation, this.pronouns, this.location).filter {
        it.isNotBlank()
    }.joinToString(" · ")
    return listOf(line1, line2).filter { it.isNotBlank() }.joinToString("\n")
}

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
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.extensions.defaultProfile"),
    level = DeprecationLevel.WARNING,
)
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
    verifiedAccounts: List<LegacyVerifiedAccount> = emptyList(),
    pronunciation: String = "",
    pronouns: String = "",
    links: List<LegacyLink>? = null,
    payments: LegacyProfilePayments? = null,
    contactInfo: LegacyProfileContactInfo? = null,
    gallery: List<LegacyGalleryImage>? = null,
    numberVerifiedAccounts: Int? = null,
    lastProfileEdit: Instant? = null,
    registrationDate: Instant? = null,
): LegacyProfile = LegacyProfile(
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

/**
 * Instantiates an empty profile with the given hash by default.
 * You can also provide other values to override the default empty values.
 */
public fun defaultProfile(
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
): Profile = Profile {
    this.hash = hash
    this.displayName = displayName
    this.profileUrl = profileUrl
    this.avatarUrl = avatarUrl
    this.avatarAltText = avatarAltText
    this.location = location
    this.description = description
    this.jobTitle = jobTitle
    this.company = company
    this.verifiedAccounts = verifiedAccounts
    this.pronunciation = pronunciation
    this.pronouns = pronouns
    this.links = links
    this.payments = payments
    this.contactInfo = contactInfo
    this.gallery = gallery
    this.numberVerifiedAccounts = numberVerifiedAccounts
    this.lastProfileEdit = lastProfileEdit
    this.registrationDate = registrationDate
}
