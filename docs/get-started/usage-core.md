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

## Fetching User Profile

Here's an example of how to fetch a user profile with an email:

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

## Fetching Avatars

AvatarService requires an authentication token to retrieve user's data. To get the token, please follow the steps described in the [Gravatar's OAuth](https://docs.gravatar.com/oauth/) section. 

To fetch avatars associated with an email:

```kotlin
coroutineScope.launch {
    when (val avatars = AvatarService().retrieveCatching("token", Email("gravatar@automattic.com").hash())) {
        is GravatarResult.Success -> {
            Log.d("Gravatar", "Avatars: ${avatars.value}")
            // Do something with the avatars
        }

        is GravatarResult.Failure -> {
            Log.e("Gravatar", "Error: ${avatars.error}")
            // Handle the error
        }
    }
}
```
