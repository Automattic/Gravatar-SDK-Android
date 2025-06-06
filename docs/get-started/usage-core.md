# Core Module usage

The `:gravatar` module provides services that you can use to interact with the Gravatar backend. 
It includes `ProfileService` and `AvatarService` that correspond to the exposed public [REST API](https://docs.gravatar.com/api/).

## AvatarUrl calculator

To create a valid Gravatar URL to fetch avatars, use the ``AvatarURL`` type.
You can create and validate an AvatarURL from a **known Gravatar image URL**, from an **email**, or an **email hash string**.

```kotlin
// Create from an email
val emailAvatarUrl = AvatarUrl(Email("gravatar@automattic.com"))

// Create from a hash
val hash = Hash("d3827f12f2a7e768e5c1d56b6e0d3354")
val hashAvatarUrl = AvatarUrl(hash)

// Create from a known Gravatar URL
val knownUrl = URL("https://www.gravatar.com/avatar/d3827f12f2a7e768e5c1d56b6e0d3354")
val urlAvatarUrl = AvatarUrl(knownUrl)

// Customize the avatar URL with query options
val customAvatarUrl = AvatarUrl(
    Email("gravatar@automattic.com"),
    AvatarQueryOptions {
        preferredSize = 200
        defaultAvatarOption = DefaultAvatarOption.MonsterId
        rating = ImageRating.ParentalGuidance
        forceDefaultAvatar = false
    }
)
val customAvatarUrlString = customAvatarUrl.url().toString()
// Result: https://www.gravatar.com/avatar/d3827(...)?d=monsterid&s=200&r=pg&f=n

// For Java users, you can use the builder pattern
// Java code
/*
AvatarQueryOptions queryOptions = new AvatarQueryOptions.Builder()
    .setPreferredSize(200)
    .setDefaultAvatarOption(DefaultAvatarOption.MonsterId)
    .setRating(ImageRating.ParentalGuidance)
    .setForceDefaultAvatar(false)
    .build();
AvatarUrl javaAvatarUrl = new AvatarUrl(new Email("gravatar@automattic.com"), queryOptions);
*/
```

## Profile Service

The `ProfileService` provides methods to retrieve and update Gravatar user profiles.

### Public functions

These functions don't require authentication and can be used to retrieve public profile information.

#### Retrieving a Profile by Email

```kotlin
coroutineScope.launch {
    when (val profile = ProfileService().retrieveCatching(Email("gravatar@automattic.com"))) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Profile: ${profile.value}")
            // Do something with the profile
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${profile.error}")
            // Handle the error
        }
    }
}
```

#### Retrieving a Profile by Hash

```kotlin
coroutineScope.launch {
    val hash = Hash("d3827f12f2a7e768e5c1d56b6e0d3354")
    when (val profile = ProfileService().retrieveCatching(hash)) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Profile: ${profile.value}")
            // Do something with the profile
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${profile.error}")
            // Handle the error
        }
    }
}
```

#### Retrieving a Profile by Username

```kotlin
coroutineScope.launch {
    when (val profile = ProfileService().retrieveByUsernameCatching("username")) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Profile: ${profile.value}")
            // Do something with the profile
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${profile.error}")
            // Handle the error
        }
    }
}
```

### Authenticated functions

These functions require an OAuth token for authentication. To get the token, please follow the steps described in the [Gravatar's OAuth](https://docs.gravatar.com/oauth/) section.

#### Checking if an Email is Associated with an Account

```kotlin
coroutineScope.launch {
    val email = Email("gravatar@automattic.com")
    val oauthToken = "your-oauth-token"

    when (val result = ProfileService().checkAssociatedEmailCatching(oauthToken, email)) {
        is GravatarResult.Success -> {
            val isAssociated = result.value
            Log.d("Gravatar", "Is email associated: $isAssociated")
            // Do something with the result
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```

#### Retrieving the Authenticated User's Profile

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"

    when (val profile = ProfileService().retrieveAuthenticatedCatching(oauthToken)) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Profile: ${profile.value}")
            // Do something with the result
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${profile.error}")
            // Handle the error
        }
    }
}
```

#### Updating a Profile

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"

    // Create an update request with the fields you want to update
    val updateRequest = UpdateProfileRequest {
        displayName = "New Display Name"
        description = "This is my updated bio"
        location = "San Francisco, CA"
        jobTitle = "Software Developer"
        company = "Automattic"
    }

    when (val result = ProfileService().updateProfileCatching(oauthToken, updateRequest)) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Updated Profile: ${result.value}")
            // Do something with the updated profile
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```

## Avatar Service

The `AvatarService` provides methods to manage Gravatar avatars. All methods in this service require authentication with an OAuth token. To get the token, please follow the steps described in the [Gravatar's OAuth](https://docs.gravatar.com/oauth/) section.

### Retrieving Avatars

Retrieve a list of available avatars for a specific email hash:

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"
    val hash = Email("gravatar@automattic.com").hash()

    when (val avatars = AvatarService().retrieveCatching(oauthToken, hash)) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Avatars: ${avatars.value}")
            // Process the list of avatars
            avatars.value?.forEach { avatar ->
                val avatarId = avatar.id
                val avatarUrl = avatar.url
                val rating = avatar.rating
                // Use the avatar information
            }
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${avatars.error}")
            // Handle the error
        }
    }
}
```

### Uploading an Avatar

Upload an image to be used as a Gravatar avatar:

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"
    val imageFile = File("/path/to/image.jpg")
    val emailHash = Email("gravatar@automattic.com").hash()

    when (val result = AvatarService().uploadCatching(
        file = imageFile,
        oauthToken = oauthToken,
        hash = emailHash,
        selectAvatar = true // Automatically set as the avatar for this email
    )) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Uploaded Avatar: ${result.value}")
            val avatarId = result.value.id
            val avatarUrl = result.value.url
            // Use the uploaded avatar information
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```

### Setting an Avatar

Set an existing avatar for a specific email hash:

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"
    val emailHash = Email("gravatar@automattic.com").hash().toString()
    val avatarId = "avatar-id-from-retrieve-call"

    when (val result = AvatarService().setAvatarCatching(
        hash = emailHash,
        avatarId = avatarId,
        oauthToken = oauthToken
    )) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Avatar set successfully")
            // Avatar was successfully set
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```

### Updating an Avatar

Update the rating or alt text of an existing avatar:

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"
    val avatarId = "avatar-id-from-retrieve-call"

    when (val result = AvatarService().updateAvatarCatching(
        avatarId = avatarId,
        oauthToken = oauthToken,
        avatarRating = Avatar.Rating.PG,
        altText = "My updated avatar description"
    )) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Updated Avatar: ${result.value}")
            // Avatar was successfully updated
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```

### Deleting an Avatar

Delete an avatar with a specific ID:

```kotlin
coroutineScope.launch {
    val oauthToken = "your-oauth-token"
    val avatarId = "avatar-id-to-delete"

    when (val result = AvatarService().deleteAvatarCatching(
        avatarId = avatarId,
        oauthToken = oauthToken
    )) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Avatar deleted successfully")
            // Avatar was successfully deleted
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${result.error}")
            // Handle the error
        }
    }
}
```
