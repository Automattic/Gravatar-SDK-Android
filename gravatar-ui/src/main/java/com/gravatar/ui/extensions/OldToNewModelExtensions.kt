package com.gravatar.ui.extensions

import com.gravatar.restapi.models.CryptoWalletAddress
import com.gravatar.restapi.models.GalleryImage
import com.gravatar.restapi.models.Link
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import com.gravatar.restapi.models.ProfilePayments
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.ui.components.ComponentState
import com.gravatar.api.models.CryptoWalletAddress as LegacyCryptoWalletAddress
import com.gravatar.api.models.GalleryImage as LegacyGalleryImage
import com.gravatar.api.models.Link as LegacyLink
import com.gravatar.api.models.Profile as LegacyProfile
import com.gravatar.api.models.ProfileContactInfo as LegacyProfileContactInfo
import com.gravatar.api.models.ProfilePayments as LegacyProfilePayments
import com.gravatar.api.models.VerifiedAccount as LegacyVerifiedAccount

internal fun LegacyVerifiedAccount.toApi2VerifiedAccount(): VerifiedAccount {
    this.let { oldVerifiedAccount ->
        return VerifiedAccount {
            serviceType = oldVerifiedAccount.serviceType
            serviceLabel = oldVerifiedAccount.serviceLabel
            serviceIcon = oldVerifiedAccount.serviceIcon
            url = oldVerifiedAccount.url
        }
    }
}

internal fun LegacyLink.toApi2Link(): Link {
    this.let { oldLink ->
        return Link {
            label = oldLink.label
            url = oldLink.url
        }
    }
}

internal fun LegacyCryptoWalletAddress.toApi2CryptoWalletAddress(): CryptoWalletAddress {
    this.let { oldCryptoWalletAddress ->
        return CryptoWalletAddress {
            label = oldCryptoWalletAddress.label
            address = oldCryptoWalletAddress.address
        }
    }
}

internal fun LegacyProfilePayments.toApi2ProfilePayments(): ProfilePayments {
    this.let { oldProfilePayments ->
        return ProfilePayments {
            links = oldProfilePayments.links.map { it.toApi2Link() }
            cryptoWallets = oldProfilePayments.cryptoWallets.map { it.toApi2CryptoWalletAddress() }
        }
    }
}

internal fun LegacyProfileContactInfo.toApi2ProfileContactInfo(): ProfileContactInfo {
    this.let { oldProfileContactInfo ->
        return ProfileContactInfo {
            homePhone = oldProfileContactInfo.homePhone
            workPhone = oldProfileContactInfo.workPhone
            cellPhone = oldProfileContactInfo.cellPhone
            email = oldProfileContactInfo.email
            contactForm = oldProfileContactInfo.contactForm
            calendar = oldProfileContactInfo.calendar
        }
    }
}

internal fun LegacyGalleryImage.toApi2GalleryImage(): GalleryImage {
    this.let { oldGalleryImage ->
        return GalleryImage {
            url = oldGalleryImage.url
        }
    }
}

internal fun LegacyProfile.toApi2Profile(): Profile {
    this.let { oldProfile ->
        return Profile {
            hash = oldProfile.hash
            displayName = oldProfile.displayName
            profileUrl = oldProfile.profileUrl
            avatarUrl = oldProfile.avatarUrl
            avatarAltText = oldProfile.avatarAltText
            location = oldProfile.location
            description = oldProfile.description
            jobTitle = oldProfile.jobTitle
            company = oldProfile.company
            verifiedAccounts = oldProfile.verifiedAccounts.map { it.toApi2VerifiedAccount() }
            pronunciation = oldProfile.pronunciation
            pronouns = oldProfile.pronouns
            links = oldProfile.links?.map { it.toApi2Link() }
            payments = oldProfile.payments?.toApi2ProfilePayments()
            contactInfo = oldProfile.contactInfo?.toApi2ProfileContactInfo()
            gallery = oldProfile.gallery?.map { it.toApi2GalleryImage() }
            numberVerifiedAccounts = oldProfile.numberVerifiedAccounts
            lastProfileEdit = oldProfile.lastProfileEdit
            registrationDate = oldProfile.registrationDate
        }
    }
}

internal fun ComponentState<LegacyProfile>.toApi2ComponentStateProfile(): ComponentState<Profile> {
    return when (this) {
        is ComponentState.Loading -> ComponentState.Loading
        is ComponentState.Loaded -> ComponentState.Loaded(loadedValue.toApi2Profile())
        else -> ComponentState.Empty
    }
}
