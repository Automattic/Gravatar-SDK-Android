<creat Avatar=README.md
<p align="center">
    <img src="/docs/images/gravatar_logo.png" />
</p>

## Gravatar-Android

[![Build status](https://badge.buildkite.com/8859512adb21ccf83f8f0aa03249356c6f972ff594bcae602d.svg?branch=trunk)](https://buildkite.com/automattic/gravatar-sdk-android)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)

The Gravatar Android SDK is a library that provides a set of UI components and utilities to fetch and display [Gravatar](https://gravatar.com/) images and profile data in Android applications.

If you're also looking to integrate Gravatar in your iOS app, check out our [Gravatar SDK for iOS](https://github.com/Automattic/Gravatar-SDK-iOS)!

## Features

#### Core services:
- [Avatar URL calculator](/docs/get-started/usage-core.md#avatarurl-calculator) based on email, email hash, and several [query options](https://docs.gravatar.com/general/images/).
- Ready-to-use asynchronous services for Gravatar REST API based on Kotlin Coroutines:
  - [ProfileService](/docs/get-started/usage-core.md#profile-service) to retrieve and update Gravatar user profiles.
  - [AvatarService](/docs/get-started/usage-core.md#avatar-service) to retrieve, upload and update user's avatars.

#### Gravatar UI:
- Display a user's avatar image through the [Avatar](/docs/get-started/usage-ui.md#avatar) component.
- [Display a profile view](/docs/get-started/usage-ui.md#profile-cards) through ready-to-use UI components.

#### Quick Editor
- [Quick Editor](/docs/get-started/usage-quickeditor.md#overview): This customizable sheet allows you to manage your avatar and Gravatar profile.
    - [Authenticate with Gravatar](/docs/get-started/usage-quickeditor.md#authentication).
    - [Quick Editor scopes](/docs/get-started/usage-quickeditor.md#quick-editor-scopes).

## Installation

To add the Gravatar SDK to your project, you can use the following Gradle dependency:

```kotlin
dependencies {
    implementation("com.gravatar:gravatar:<version>")
    // OR
    implementation("com.gravatar:gravatar-ui:<version>")
    // OR
    implementation("com.gravatar:gravatar-quickeditor:<version>")
}
```

Additionally, if you're using version 2.3.1 or older of the SDK, you need to add custom Gradle repository:

```kotlin
repositories {
    maven {
        url = uri("https://a8c-libs.s3.amazonaws.com/android")
    }
}
```

For further details on how to integrate in your own app please take a look at our [Get Started guide](/docs/get-started/get-started.md). You'll find a quick overview of how to get an API key and use the library in your projects.

## Documentation

You can find some detailed articles, tutorials and API docs via these links:

- [Getting started](/docs/get-started/get-started.md)
- [Gravatar docs](/docs/get-started/usage-core.md)
  - [Uploading an Avatar](/docs/get-started/usage-core.md#uploading-an-avatar)
    - Let a user to update their avatar.
  - [Fetching Profile Information](/docs/get-started/usage-core.md#profile-service)
    - Fetch a public Gravatar profile.
  - [AvatarURL Calculator](/docs/get-started/usage-core.md#avatarurl-calculator)
    - Create and validate Gravatar image URLs
- [Gravatar UI docs](/docs/get-started/usage-ui.md)
  - [Avatar component](/docs/get-started/usage-ui.md#avatar)
    - Use the Avatar component to display a user's avatar image.
  - [Profile view designs](/docs/get-started/usage-ui.md#profile-cards)
    - We offer a variety of profile view layouts for different usecases.
- [Gravatar Quick Editor docs](/docs/get-started/usage-quickeditor.md)
  - [Authentication](/docs/get-started/usage-quickeditor.md#authentication)
    - Set up the Gravatar OAuth2 to unlock some features.
  - [Quick Editor](/docs/get-started/usage-quickeditor.md#overview)
    - This customizable sheet allows users to update their avatars. Available for both UIKit and SwiftUI.

For those looking for a full API documentation: [click here](https://automattic.github.io/Gravatar-SDK-Android/current/index.html).

## For Maintainers

If you're contributing to or maintaining this project, check out [SDK's technical details](/docs/sdk-technical-details/sdk-technical-details.md).

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## License

Gravatar-SDK-Android is an open source project covered by the [Mozilla Public License Version 2.0](LICENSE.md).
