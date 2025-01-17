
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

First step is to add the maven repositories to the file where you manage your the dependency resolution and the right dependencies to the `build.gradle` file:

### Add the Gravatar dependencies to your project

```groovy
repositories {
    maven {
        url "https://a8c-libs.s3.amazonaws.com/android"
    }
}
```

```groovy
dependencies {
    implementation ("com.gravatar:gravatar:<version>")
    implementation ("com.gravatar:gravatar-ui:<version>")
    implementation ("com.gravatar:gravatar-quickeditor:<version>")
}
```

### Store the Gravatar API key in your app

There are many ways to store the Gravatar API key in your app. The best way to do this depends on your app's architecture and requirements and how you're already storing other sensitive information. Make sure to avoid hardcoding the API key in your app's code and make sure to avoid storing it in a public repository.

One way to store the API key in your app is by adding it to the `local.properties` file:

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
Gravatar.apiKey(BuildConfig.GRAVATAR_API_KEY)
    .context(appContext) // Optional but highly encouraged.
```

# Usage

The Gravatar SDK is separated into three modules: [`:gravatar`](#REST-API-Services), [`:gravatar-ui`](#UI-components) and [`:gravatar-quickeditor`](#Quick-Editor). They can be used all together or you can pick the one that suits your needs.
Below you can find description of how to use each module.

## Core Module

The :gravatar module provides convenience classes to interact with Gravatar’s public [REST API](https://docs.gravatar.com/api/), enabling easy access to user profiles and avatars.

For details on how to use the `:gravatar` module, see [Core Module Usage](usage-core.md).

## UI Module

The :gravatar-ui module provides customizable Jetpack Compose components, such as profile cards of various sizes and layouts, for displaying Gravatar user information in your app.

For details on how to use the `:gravatar-ui` module, see [UI Module Usage](usage-ui.md).

## Quick Editor

The :gravatar-quickeditor module simplifies avatar management by offering a fully featured, customizable UI for editing avatars directly within your app. 

It supports OAuth authentication with built-in flow handling or token-based integration, provides flexible options for Jetpack Compose and traditional Views, and includes features like secure token storage, cache-busting for immediate updates, and easy theming. Designed to improve user experience and make development straightforward, it requires minimal configuration while adhering to modern Android practices.

For details on how to use the `:gravatar-quickeditor` module, see [Quick Editor Module Usage](usage-quickeditor.md).
