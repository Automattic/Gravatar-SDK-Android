# UI Module usage

The `:gravatar-ui` module provides ready-to-use Jetpack Compose UI components for displaying Gravatar profiles and avatars in your Android application. These components range from simple atomic elements like avatars and display names to complete profile cards with various layouts and information density.

The UI components handle loading states, error states, and data presentation automatically, making it easy to integrate Gravatar into both Compose-based and traditional View-based applications.

## Avatar

The `Avatar` component displays a user's avatar image. It is a simple as providing an email address.

```kotlin
Avatar(
    email = Email("gravatar@automattic.com"),
    size = 64.dp,
    // Optional query options
    avatarQueryOptions = AvatarQueryOptions {
        defaultAvatarOption = DefaultAvatarOption.MonsterId
        rating = ImageRating.ParentalGuidance
    }
)
```

## Profile Cards

The `:gravatar-ui` module provides various different types of Profile cards to suit your needs. They vary in size and the number of information presented to the user.

| Profile                                  | ProfileSummary                        | LargeProfile                        | LargeProfileSummary                         |
|------------------------------------------|---------------------------------------|-------------------------------------|---------------------------------------------|
| ![](/docs/images/profile_light_demo.png) | ![](/docs/images/profile_summary.png) | ![](/docs/images/large_profile.png) | ![](/docs/images/large_profile_summary.png) |

### Add a Profile Component to your Jetpack Compose App

You can use the following code snippet to integrate the Gravatar profile in your app. This is a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard` composable.

```kotlin
@Composable
fun GravatarProfileSummary(emailAddress: String = "gravatar@automattic.com") {
    // Create a ProfileService instance
    val profileService = ProfileService()

    // Set the default profile state to loading
    var profileState: ComponentState<Profile> by remember { mutableStateOf(ComponentState.Loading, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        // Set the profile state to loading
        profileState = ComponentState.Loading
        // Fetch the user profile
        when (val result = profileService.retrieve(Email(emailAddress))) {
            is GravatarResult.Success -> {
                // Update the profile state with the loaded profile
                result.value.let {
                    profileState = ComponentState.Loaded(it)
                }
            }
            is GravatarResult.Failure -> {
                // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
                // Here we log the error, but ideally we should show an error to the user.
                Log.e("Gravatar", result.error.name)
                // Set the Empty state on error
                profileState = ComponentState.Empty
            }
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(profileState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}
```

### Add a Profile Component to your View-based App

If you are using a View-based app, you can use the following code snippet to integrate the Gravatar profile in your app. This is an example of a very simple component that fetches a Gravatar profile and displays it in a `ProfileCard`.

In your layout xml file, you need to add the following view:

```xml
<!-- Your layout file (e.g., activity_main.xml) -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Other views -->

    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/gravatarComposeView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <!-- Other views -->

</androidx.constraintlayout.widget.ConstraintLayout>
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
    var profileState: ComponentState<Profile> by remember { mutableStateOf(ComponentState.Loading, neverEqualPolicy()) }

    // We wrap the fetch call in a LaunchedEffect to fetch the profile when the composable is first launched, but this
    // could be triggered by a button click, a text field change, etc.
    LaunchedEffect(emailAddress) {
        // Set the profile state to loading
        profileState = ComponentState.Loading
        // Fetch the user profile
        when (val result = profileService.retrieve(Email(emailAddress))) {
            is GravatarResult.Success -> {
                // Update the profile state with the loaded profile
                result.value.let {
                    profileState = ComponentState.Loaded(it)
                }
            }
            is GravatarResult.Failure -> {
                // An error can occur when a profile doesn't exist, if the phone is in airplane mode, etc.
                // Here we log the error, but ideally we should show an error to the user.
                Log.e("Gravatar", result.error.name)
                // Set the Empty state on error
                profileState = ComponentState.Empty
            }
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(profileState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}
```

More information on the official documentation: [Using Compose in Views](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).

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
