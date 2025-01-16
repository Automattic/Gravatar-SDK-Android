# Core Module usage

The `:gravatar` module provides services that you can use to interact with the Gravatar backend. It includes `ProfileService` and `AvatarService` that correspond to the exposed public [REST API](https://docs.gravatar.com/api/).

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
