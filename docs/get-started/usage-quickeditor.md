# Quick Editor Module usage

## Table of Contents
* [Overview](#overview)
* [Authentication](#authentication)
    * [1. Let the Quick Editor handle the OAuth flow](#1-let-the-quick-editor-handle-the-oauth-flow)
    * [2. Obtain the token yourself and provide it to the Quick Editor](#2-obtain-the-token-yourself-and-provide-it-to-the-quick-editor)
* [Quick Editor Scopes](#quick-editor-scopes)
* [Update Handler](#update-handler)
* [Activity/Fragment compatibility](#activityfragment-compatibility)
* [Cache busting](#cache-busting)
* [Android Permissions](#android-permissions)
* [Version migrations](#version-migrations)

## Overview

The `:gravatar-quickeditor` module provides a fully featured component that allows the user to modify their Gravatar profile information, including their avatar and "About" details, without leaving your app.

The Quick Editor's functionality can be tailored using `QuickEditorScopeOption` to define what the user can edit. This allows for a focused experience depending on the desired interaction:
*   **`QuickEditorScopeOption.avatarPicker()`**: Launches the Quick Editor focused solely on avatar management. Users can select an existing image, upload a new one, or remove their current Gravatar through a streamlined interface. This scope is ideal when you only need to offer avatar editing capabilities.
*   **`QuickEditorScopeOption.aboutEditor()`**: This option presents the Quick Editor with tools to modify the user's "About" profile section. This typically includes details such as their display name, full name, pronouns, a public description or bio, and current location.
*   **`QuickEditorScopeOption.avatarPickerAndAboutEditor()`**: For a comprehensive profile editing experience, this combined scope allows users to modify both their avatar and their "About Me" details. The Quick Editor will provide a way to navigate between the avatar editing and "About" sections seamlessly without needing to close and re-launch the component.

For all of this to be possible, the QuickEditor needs an authorization token to perform requests on behalf of the user.

## Authentication

### 1. Let the Quick Editor handle the OAuth flow

Quick Editor can handle the heavy lifting of running the full OAuth flow, so you don't have to do that. We will still need a few things from you.
First, you have to go to [OAuth docs](https://docs.gravatar.com/oauth/) and create your Application. Define the `Redirect URLs`.

> Keep in mind that you need to use the `https` scheme. Internally, QuickEditor uses Implicit OAuth flow (`response_type=token`) and for security reasons, the server doesn't allow custom URL schemes.

For the sake of this example let's assume the redirect URL is `https://yourhost.com/redirect-url`.

In your `AndroidManifest.xml` you need to setup `GravatarOAuthActivity` by adding the `<activity>` tag. 
This Activity is launched by the Quick Editor when starting the OAuth flow and it handles the redirect URL to retrieve the token.

```xml
<activity
    android:name="com.gravatar.quickeditor.ui.oauth.GravatarOAuthActivity"
    tools:node="merge">
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
            scopeOption = QuickEditorScopeOption.avatarPicker()
        },
        authenticationMethod = AuthenticationMethod.OAuth(
            OAuthParams {
                clientId = "{YOUR_CLIENT_ID}"
                redirectUri = "{YOUR_REDIRECT_URL}" // In our example this would be https://yourhost.com/redirect_url
            },
        ),
        updateHandler = { updateType -> ... },
        onDismiss = { gravatarQuickEditorDismissReason ->
            showBottomSheet = false
            ...
        },
    )
}
```

With the provided `clientId` and the `redirectUrl` Quick Editor will launch and handle the full OAuth flow. Once obtained, the token will be stored in an encrypted Data Store.
This token will be later used in subsequent Quick Editor launches to make the user experience more seamless by not having to go through the OAuth flow each time.

When the user logs out form the app, make sure to run:

```kotlin
GravatarQuickEditor.logout(Email("{USER_EMAIL}"))
```

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
            scopeOption = QuickEditorScopeOption.avatarPicker()
        },
        authenticationMethod = AuthenticationMethod.Bearer("{TOKEN}"),
        updateHandler = { updateType -> ... },
        onDismiss = { gravatarQuickEditorDismissReason ->
            showBottomSheet = false
            ...
        },
    )
}
```

## Quick Editor Scopes

Quick Editor scopes were already briefly described in the Overview section, but here is a more detailed explanation of each scope option:

### `QuickEditorScopeOption.avatarPicker()`

This scope option focuses solely on avatar management. When using this option, the Quick Editor will present a user interface that allows users to:
- Select an avatar from the uploaded images.
- Upload an existing image from their device or take a new one.
- Remove their current Gravatar avatar.
- Modify the Alt Text for the avatar.
- Modify the rating of the avatar.
- Download any of the already uploaded avatars.

**AvatarPicker** scope is configured via `AvatarPickerConfiguration` class. Currently it supports one parameter: `AvatarPickerContentLayout` that defines how the user's avatar will be displayed. It can be either `Horizontal` or `Vertical`. The default value is `Horizontal`.

Here's an example of how to configure the `AvatarPicker` scope:

```kotlin
GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams = GravatarQuickEditorParams {
        ...
        scopeOption = QuickEditorScopeOption.avatarPicker(
            config = AvatarPickerConfiguration(
                contentLayout = AvatarPickerContentLayout.Vertical,
            ),
        )
    },
    ...
)
```

| Horizontal                                     | Vertical                                     |
|------------------------------------------------|----------------------------------------------|
| ![](/docs/images/avatar_picker_horizontal.png) | ![](/docs/images/avatar_picker_vertical.png) |

### `QuickEditorScopeOption.aboutEditor()`

This scope option allows users to edit their "About" section in Gravatar. To check the currently supported fields, refer to the [AboutInputField](https://github.com/Automattic/Gravatar-SDK-Android/blob/adam/GRA-128/gravatar-quickeditor/src/main/java/com/gravatar/quickeditor/ui/editor/AboutInputField.kt) class.

In order to configure the `AboutEditor` scope, you can use the `AboutEditorConfiguration` class. This class allows you to specify which fields should be editable by the user.

Here's an example of how to configure the `AboutEditor` scope:

```kotlin
GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams = GravatarQuickEditorParams {
        ...
        scopeOption = QuickEditorScopeOption.aboutEditor(
            config = AboutEditorConfiguration(
                fields = setOf(
                    AboutInputField.DisplayName,
                    AboutInputField.AboutMe,
                ),
            ),
        )
    },
    ...
)
```

| All fields                                    | Only two selected                             |
|-----------------------------------------------|-----------------------------------------------|
| ![](/docs/images/about_editor_all_fields.png) | ![](/docs/images/about_editor_two_fields.png) |

### `QuickEditorScopeOption.avatarPickerAndAboutEditor()`

This scope option combines both the avatar management and "About" section editing functionalities. It allows users to seamlessly switch between editing their avatar and updating their profile information without needing to close and reopen the Quick Editor.

To configure the `AvatarPickerAndAboutEditor` scope, you can use the `AvatarPickerAndAboutEditorConfiguration` class. It takes all the parameters from both `AvatarPickerConfiguration` and `AboutEditorConfiguration`, allowing you to define how the avatar picker and the "About" editor should behave. 
On top of that it allows you to define the initial page that will be displayed when the Quick Editor is launched. The initial page can be either `AvatarPicker` or `AboutEditor`.

Here's an example of how to configure the `AvatarPickerAndAboutEditor` scope:

```kotlin
GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams = GravatarQuickEditorParams {
        ...
        scopeOption = QuickEditorScopeOption.avatarPickerAndAboutEditor(
            config = AvatarPickerAndAboutEditorConfiguration(
                contentLayout = AvatarPickerContentLayout.Vertical,
                fields = AboutInputField.all,
                initialPage = AvatarPickerAndAboutEditorConfiguration.Page.AvatarPicker,
           ),
        )
    },
    ...
)
```

| Avatar Picker and About Editor         |
|----------------------------------------|
| ![](/docs/images/avatar_and_about.gif) |


### Update Handler

When using the Quick Editor, you need to provide an `updateHandler` that will be called when the user makes changes to their profile. This allows you to handle updates in your app, such as refreshing the UI or saving changes to a local database.

Each invocation of the `updateHandler` will provide a [QuickEditorUpdateType](https://github.com/Automattic/Gravatar-SDK-Android/blob/trunk/gravatar-quickeditor/src/main/java/com/gravatar/quickeditor/ui/editor/UpdateHandler.kt) that indicates what type of update was made. It can be either `AvatarPickerResult` or `AboutEditorResult`, depending on whether the user changed their avatar or their "About" section.

Here's an example of how to implement the `updateHandler`:

```kotlin
GravatarQuickEditorBottomSheet(
    ...
    updateHandler = { updateType ->
        when (updateType) {
            is AvatarPickerResult -> {
                // Handle avatar update
            }
            is AboutEditorResult -> {
                // Handle about section update
            }
        }
    },
)
```

## Activity/Fragment compatibility

Gravatar SDK is built with Compose but we do provide some helper functions to launch the Quick Editor from an Activity or Fragment. Here's an example from an Activity:

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

## Cache busting

When an avatar is modified, the change is applied immediately, but it may take a few seconds to propagate through the Gravatar servers. As a result, requesting the avatar quickly using the usual URL might sometimes return the previous avatar.

To ensure you receive the updated avatar after a change, you can use a cache buster. Our SDK provides methods to assist you in this process.

### Using AvatarUrl

The `AvatarUrl` method accepts an optional parameter that we'll add as a cache buster:

```kotlin
public fun url(cacheBuster: String? = null): URL
```

You can use any string as a cache buster, but ensure that you don't repeat the value when you want to refresh the avatar. A timestamp is a good example of a unique cache buster.

### Using the Avatar UI Component

If you're using our UI Avatar component, you can simply enable the `forceRefresh` parameter when you want to use the cache buster. We'll manage everything for you, updating the cache buster in every recomposition while the `forceRefresh` parameter remains true.

```kotlin
@Composable
public fun Avatar(
    state: ComponentState<Profile>,
    size: Dp,
    modifier: Modifier = Modifier,
    avatarQueryOptions: AvatarQueryOptions? = null,
    forceRefresh: Boolean = false,
)
```

By setting `forceRefresh` to true, you ensure that the avatar is always fetched with the latest changes. Although this will result in a new avatar, it must be used carefully as the image will be refreshed on each recomposition, which may not be ideal for performance.

If you want a more fine-grained control with the `Avatar` component you can use the version that takes the URL as a parameter and pass the URL with the cacheBuster value created with the `AvatarUrl` class.

```kotlin
@Composable
public fun Avatar(
    state: ComponentState<String>,
    size: Dp,
    modifier: Modifier = Modifier,
)
```

## Android Permissions

The Quick Editor module requires certain permissions to function correctly. Below is a table listing the permissions and the reasons why they are needed:

| Permission                  | Reason                                                                                           |
|-----------------------------|--------------------------------------------------------------------------------------------------|
| `INTERNET`                  | Required to make network requests to the Gravatar API.                                           |
| `WRITE_EXTERNAL_STORAGE`    | Allows the app to save images to the device storage on Android 9 and lower via Download Manager. |

If you use the same permission with different configurations, you might end up with conflicts.

## Version migrations

When updating the SDK, you might need to migrate your code to the new version. Here is the list of all the migrations:

| Versions                | Intructions                                     |
|-------------------------|-------------------------------------------------|
| 2.x - 2.3.0             | [2.x-2.3.0](../version-migrations/2.x-2.3.0.md) |

