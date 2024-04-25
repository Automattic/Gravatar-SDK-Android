
# Get Started with Gravatar-SDK-Android


## Installation

First step it to add the maven repositories and the right dependencies to the `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://a8c-libs.s3.amazonaws.com/android"
    }
    // Jitpack is used to fetch the uCrop library. Required only for gravatar-ui module.
    maven {
        url "https://jitpack.io"
        content {
            includeModule("com.github.yalantis", "ucrop")
        }
    }
}

dependencies {
    implementation ("com.gravatar:gravatar:<version>")
    implementation ("com.gravatar:gravatar-ui:<version>")
}
```

## Usage

### Add a Profile Component to your Jetpack Compose App

Then, you can use the following code snippet to integrate the Gravatar profile in your app. This is a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard` composable.

```kotlin

@Composable
fun SimpleGravatarProfileIntegration(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Create a mutable state for the user profile
    var profile: UserProfile? by remember { mutableStateOf(null, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(true) {
        try {
            // Fetch the user profile
            profile = profileService.fetchSuspend(Email(emailAddress).hash().toString()).entry.first()
        } catch (exception: ProfileService.FetchException) {
            // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
            // Here we log the error, but ideally we should show an error to the user.
            Log.e("Gravatar", exception.errorType.name)
        }
    }
    // Show the profile as a ProfileCard
    ProfileCard(profile)
}
```

Once you have integrated this component into your app, you should see:

<img src="screenshot-simple-integration.png" alt="screenshot of the profile component" width="300"/>

### Add a Profile Component to your View-based App

If you are using a View-based app, you can use the following code snippet to integrate the Gravatar profile in your app. This is an example of a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard`.

```kotlin

class ExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SimpleGravatarProfileIntegration("gravatar@automattic.com")
            }
        }
    }
}


@Composable
fun SimpleGravatarProfileIntegration(emailAddress: String) {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Create a mutable state for the user profile
    var profile: UserProfile? by remember { mutableStateOf(null, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(true) {
        try {
            // Fetch the user profile
            profile = profileService.fetchSuspend(Email(emailAddress).hash().toString()).entry.first()
        } catch (exception: ProfileService.FetchException) {
            // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
            // Here we log the error, but ideally we should show an error to the user.
            Log.e("Gravatar", exception.errorType.name)
        }
    }
    // Show the profile as a ProfileCard
    ProfileCard(profile)
}
```

More information on the official documentation: [Using Compose in Views](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).

## Customization

### Override the GravatarTheme

