# Quick Editor Module usage

The `:gravatar-quickeditor` module provides a fully featured component that allows the user to modify their avatar without leaving your app.

To do that the QuickEditor needs an authorization token to perform requests on behalf of the user. There are two ways for that:

### 1. Let the Quick Editor handle the OAuth flow

#### 1.1 Using you own activity with `android:launchMode="singleTask"` (Recommended)

Quick Editor can handle the heavy lifting of running the full OAuth flow, so you don't have to do that. We will still need a few things from you.
First, you have to go to [OAuth docs](https://docs.gravatar.com/oauth/) and create your Application. Define the `Redirect URLs`.

> Keep in mind that you need to use the `https` scheme. Internally, QuickEditor uses Implicit OAuth flow (`response_type=token`) and for security reasons, the server doesn't allow custom URL schemes.

For the sake of this example let's assume the redirect URL is `https://yourhost.com/redirect-url`.

In your `AndroidManifest.xml` you need to add an `<intent-filter>` and the `android:launchMode="singleTask"` to the activity that will
launch the Quick Editor (or the last/main activity depending on your app architecture). This is important because the Quick Editor will be waiting for the `onNewIntent()` callback to handle OAuth redirection.

```xml
<activity
    android:name=".YourActivity"
    android:launchMode="singleTask"
    ...>
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:scheme="https"
            android:host="yourhost.com"
            android:pathPrefix="/redirect-url"
        />
    </intent-filter>
</activity>
```

> Make sure to follow official [Android App Links documentation](https://developer.android.com/training/app-links#android-app-links) to properly setup App Link.

Once you've added that you can add the Quick Editor to your Compose screen:

```kotlin
var showBottomSheet by rememberSaveable { mutableStateOf(false) }

if (showBottomSheet) {
    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = GravatarQuickEditorParams {
            email = Email("{USER_EMAIL}")
            avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
        },
        authenticationMethod = AuthenticationMethod.OAuth(
            OAuthParams {
                clientId = "{YOUR_CLIENT_ID}"
                redirectUri = "{YOUR_REDIRECT_URL}" // In our example this would be https://yourhost.com/redirect_url
            },
        ),
        onAvatarSelected = { ... },
        onDismiss = { gravatarQuickEditorDismissReason ->
            showBottomSheet = false
            ...
        },
    )
}
```

With the provided `clientId` and the `redirectUrl` Quick Editor can launch and handle the full OAuth flow. Once obtained the token will be stored in an encrypted Data Store.
This token will be later used in subsequent Quick Editor launches to make the user experience more seamless by not having to go through the OAuth flow each time.

When the user logs out form the app, make sure to run:

```kotlin
GravatarQuickEditor.logout(Email("{USER_EMAIL}"))
```

#### 1.2 Using the provided activity

If using an activity with `android:launchMode="singleTask"` is not an option, you can use the provided activity. With this option, you don't need to modify how your activities are set up.

You need to add the provided activity to your `AndroidManifest.xml`:

```xml
<activity
    android:name="com.gravatar.quickeditor.ui.GravatarQuickEditorActivity"
    tools:node="merge">

    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:scheme="https"
            android:host="yourhost.com"
            android:pathPrefix="/redirect-url"
        />
    </intent-filter>
</activity>
```

_Note the important difference here: the `tools:node="merge"` attribute. This is necessary to merge the intent filter with the one defined in the library._

The `GravatarQuickEditorActivity` defines an Activity Result contract that you can use to launch the Quick Editor and handle the result. Here's an example of how you can use it:

```kotlin
private val getQEResult = registerForActivityResult(GetQuickEditorResult()) { quickEditorResult ->
        when (quickEditorResult) {
            GravatarQuickEditorResult.AVATAR_SELECTED -> { ... }

            GravatarQuickEditorResult.DISMISSED -> { ... }

            else -> { ... }
        }
}

getQEResult.launch(
    GravatarQuickEditorActivity.GravatarEditorActivityArguments(
        GravatarQuickEditorParams { ... },
        AuthenticationMethod.OAuth(
            OAuthParams { ... },
        ),
    ),
)
```

It's important to note that using the `GravatarQuickEditorActivity` you'll only receive the result of the Quick Editor when it's dismissed not instantly as with using the `@Composable` component from your `singleTask` activity (see [Section 1.1](#11-using-you-own-activity-with-androidlaunchmodesingletask-recommended)).

In the `demo-app` you can find a detailed implementation showing how to use the provided activity. See `QuickEditorTestActivity`.

#### Exclude Data Store files from Android backup (optional, but recommended)

Data Store files are subject to Android backups. Encrypted files from the backup won't work when restored on a different device so we have to exclude those files.
It is encouraged to create those files or copy paste below rules to your own respective files.

<details>
  <summary>Instructions</summary>

In `AndroidManifest.xml` add the below lines. If you already have them, you can skip this step.

```xml
<application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        ...>
```

Create [@xml/data_extraction_rules](https://github.com/Automattic/Gravatar-SDK-Android/blob/trunk/gravatar-quickeditor/src/main/res/xml/data_extraction_rules.xml) or modify your file with the below rules.

```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <exclude
            domain="sharedpref"
            path="__androidx_security_crypto_encrypted_file_pref__.xml" />
        <exclude
            domain="file"
            path="datastore/quick-editor-preferences.preferences_pb" />
    </cloud-backup>
    <device-transfer>
        <exclude
            domain="sharedpref"
            path="__androidx_security_crypto_encrypted_file_pref__.xml" />
        <exclude
            domain="file"
            path="datastore/quick-editor-preferences.preferences_pb" />
    </device-transfer>
</data-extraction-rules>
```

Create [@xml/backup_rules](https://github.com/Automattic/Gravatar-SDK-Android/blob/trunk/gravatar-quickeditor/src/main/res/xml/backup_rules.xml) or modify your file with the below rules.

```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <exclude
        domain="sharedpref"
        path="__androidx_security_crypto_encrypted_file_pref__.xml" />
    <exclude
        domain="file"
        path="datastore/quick-editor-preferences.preferences_pb" />
</full-backup-content>
```

</details>

### 2. Obtain the token yourself and provide it to the Quick Editor

Quick Editor can be launched with the provided token. To obtain it, you have to follow the [OAuth docs](https://docs.gravatar.com/oauth/) and implement the OAuth flow within your app.

Once you have the token, here's how you can embed the QuickEditor in your Compose screen:

```kotlin
var showBottomSheet by rememberSaveable { mutableStateOf(false) }

if (showBottomSheet) {
    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = GravatarQuickEditorParams {
            email = Email("{USER_EMAIL}")
            avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
        },
        authenticationMethod = AuthenticationMethod.Bearer("{TOKEN}"),
        onAvatarSelected = { ... },
        onDismiss = { gravatarQuickEditorDismissReason ->
            showBottomSheet = false
            ...
        },
    )
}
```

### Activity/Fragment compatibility

Gravatar SDK is built with Compose but we do provide some helper functions to launch the Quick Editor from an Activity or Fragment. Here's an example an Activity:

```kotlin
GravatarQuickEditor.show(
    activity = this,
    gravatarQuickEditorParams = GravatarQuickEditorParams {
        email = Email("{USER_EMAIL}")
        avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
    },
    authenticationMethod = AuthenticationMethod.Bearer("{TOKEN}"),
    onAvatarSelected = {  ... },
    onDismiss = { gravatarQuickEditorDismissReason -> ... },
)
```

### Cache busting

When an avatar is modified, the change is applied immediately, but it may take a few seconds to propagate through the Gravatar servers. As a result, requesting the avatar quickly using the usual URL might sometimes return the previous avatar.

To ensure you receive the updated avatar after a change, you can use a cache buster. Our SDK provides methods to assist you in this process.

#### Using AvatarUrl

The `AvatarUrl` method accepts an optional parameter that we'll add as a cache buster:

```kotlin
public fun url(cacheBuster: String? = null): URL
```

You can use any string as a cache buster, but ensure that you don't repeat the value when you want to refresh the avatar. A timestamp is a good example of a unique cache buster.

#### Using the Avatar UI Component

If you're using our UI Avatar component, you can simply enable the `forceRefresh` parameter when you want to use the cache buster. We'll manage everything for you, updating the cache buster in every recomposition while the `forceRefresh` parameter remains true.

```kotlin
@Composable
public fun Avatar(
    profile: Profile,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
    forceRefresh: Boolean = false,
)
```

By setting `forceRefresh` to true, you ensure that the avatar is always fetched with the latest changes.

### Android Permissions

The Quick Editor module requires certain permissions to function correctly. Below is a table listing the permissions and the reasons why they are needed:

| Permission                  | Reason                                                                                           |
|-----------------------------|--------------------------------------------------------------------------------------------------|
| `INTERNET`                  | Required to make network requests to the Gravatar API.                                           |
| `WRITE_EXTERNAL_STORAGE`    | Allows the app to save images to the device storage on Android 9 and lower via Download Manager. |

If you use the same permission with different configurations, you might end up with conflicts.
