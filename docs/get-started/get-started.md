
# Get Started with Gravatar-SDK-Android

## Creating an API key

You'll need an API key to use the SDK's full capabilities. You can use some features without an API Key, but you'll receive limited information, and stricter rate limits may apply, so we highly encourage you to create one in the [developers' portal](https://gravatar.com/developers/). 

You can create the API Key as follows:

1. Login in to your Gravatar account (or the Gravatar account you want to use to generate the key)
2. Navigate to the [developers portal](https://gravatar.com/developers/)
3. Tap on [`Create new Application`](https://gravatar.com/developers/new-application)
4. Fill the required data and follow the flow

<img src="screenshot-developers-portal.png" alt="Creating an API Key in the developers portal" width="600"/>

5. You'll get your Gravatar API Key. Save it in a safe place!

## Installation

First step it to add the maven repositories to the file where you manage your the dependency resolution and the right dependencies to the `build.gradle` file:

### Add the Gravatar dependencies to your project

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
```

```groovy
dependencies {
    implementation ("com.gravatar:gravatar:<version>")
    implementation ("com.gravatar:gravatar-ui:<version>")
}
```

### Store the Gravatar API key in your app

There are many ways to store the Gravatar API key in your app. The best way to do this depends on your app's architecture and requirements and how you're already storing other sensitive information. Make sure to avoid hardcoding the API key in your app's code and make sure to avoid storing it in a public repository.

One way to API key in your app by adding it to the `local.properties` file:

```properties
gravatar.api.key = REPLACE_ME
```

Then update your gradle file to read the API key from the `local.properties` file and put it in the generated `BuildConfig` class:

```groovy
android {
    buildFeatures.buildConfig = true

    val properties = Properties()
    properties.load(FileInputStream(project.rootProject.file("local.properties")))
    buildConfigField("String", "GRAVATAR_API_KEY", "\"${properties["gravatar.api.key"]}\"")
}
```

Then you can access the API key in your app's code like this:

```kotlin
Gravatar.initialize(BuildConfig.GRAVATAR_API_KEY)
```

## Usage

### Add a Profile Component to your Jetpack Compose App

Then, you can use the following code snippet to integrate the Gravatar profile in your app. This is a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard` composable.

```kotlin
@Composable
fun GravatarProfileSummary(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Set the default profile state to loading
    var profileState: UserProfileState by remember { mutableStateOf(UserProfileState.Loading, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        // Set the profile state to loading
        profileState = UserProfileState.Loading
        // Fetch the user profile
        when (val result = profileService.fetch(Email(emailAddress))) {
            is Result.Success -> {
                // Update the profile state with the loaded profile
                result.value.let {
                    profileState = UserProfileState.Loaded(it)
                }
            }
            is Result.Failure -> {
                // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
                // Here we log the error, but ideally we should show an error to the user.
                Log.e("Gravatar", result.error.name)
                // Set the Empty state on error
                profileState = UserProfileState.Empty
            }
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(profileState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}
```

Once you have integrated this component into your app, you should see:

<img src="screenshot-simple-integration.png" alt="screenshot of the profile component" width="300"/>

### Add a Profile Component to your View-based App

If you are using a View-based app, you can use the following code snippet to integrate the Gravatar profile in your app. This is an example of a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard`.

In your layout xml file, you need to add the following view:

```xml
... 
    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/gravatarComposeView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

...

```

From the code, you can set the composable code to that view as follows:
```kotlin

class ExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        findViewById<ComposeView>(R.id.gravatarComposeView).setContent {
            GravatarProfileSummary()
        }
    }
}


@Composable
fun GravatarProfileSummary(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Set the default profile state to loading
    var profileState: UserProfileState by remember { mutableStateOf(UserProfileState.Loading, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        // Set the profile state to loading
        profileState = UserProfileState.Loading
        // Fetch the user profile
        val result = profileService.fetch(Email(emailAddress))
        (result as? Result.Success)?.value?.let {
            profileState = UserProfileState.Loaded(it)
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(profileState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}
```

More information on the official documentation: [Using Compose in Views](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).

### Fetch user profile data

It is possible that you want to use your own UI components with Gravatar data. In that case, you can use the SDK to fetch any user profile data given their username, email, or hash identifier. 

For example, using the user's email:

```kotlin
coroutineScope.launch {
    when (val profile = ProfileService().fetch(Email("gravatar@automattic.com"))) {
        is Result.Success -> {
            Log.d("Gravatar", "Profile: ${profile.value}")
            // Do something with the profile
        }

        is Result.Failure -> {
            Log.e("Gravatar", "Error: ${profile.error}")
            // Handle the error
        }
    }
}
```

## Customization

### Override the GravatarTheme

You're free to customize the Gravatar profile component to fit your app's design. You can do this by overriding the `GravatarTheme` in your app's theme.

```kotlin
CompositionLocalProvider(LocalGravatarTheme provides object : GravatarTheme {
    // Override theme colors
    override val colorScheme: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme.copy(outline = Color.LightGray)

    // Override typography style
    override val typography: Typography
        @Composable
        get() = MaterialTheme.typography.copy(
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        )
}) {
    LargeProfileSummary(profile = userProfile)
}
```