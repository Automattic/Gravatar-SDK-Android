<p align="center">
    <img src="/docs/images/gravatar_logo.png" />
</p>

## Gravatar-Android

[![Build status](https://badge.buildkite.com/8859512adb21ccf83f8f0aa03249356c6f972ff594bcae602d.svg?branch=trunk)](https://buildkite.com/automattic/gravatar-sdk-android)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)

The Gravatar Android SDK is a library that provides a set of UI components and utilities to fetch and display [Gravatar](https://gravatar.com/) images and profile data in Android applications.

If you're also looking to integrate Gravatar in your iOS app, check out our [Gravatar SDK for iOS](https://github.com/Automattic/Gravatar-SDK-iOS)!

<p align="center">
    <img src="docs/images/profile_light_demo.png" />
</p>

## Features

- Ready-to-use asynchronous services for Gravatar REST API based on Kotlin Coroutines.
- Avatar URL calculator based on email and several query options.
- Display a profile view or an avatar through ready-to-use Jetpack Compose UI components.
- QuickEditor: This customizable sheet allows you to manage your avatar and Gravatar profile. You can select an existing avatar or upload a new one, and it provides a summary of your Gravatar profile.

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

You can find the full API documentation [here](https://automattic.github.io/Gravatar-SDK-Android/current/index.html).

## For Maintainers

If you're contributing to or maintaining this project, check out [SDK's technical details](/docs/sdk-technical-details/sdk-techincal-details.md).

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## License

Gravatar-SDK-Android is an open source project covered by the [Mozilla Public License Version 2.0](LICENSE.md).
