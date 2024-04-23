# Gravatar-Android

[![Build status](https://badge.buildkite.com/8859512adb21ccf83f8f0aa03249356c6f972ff594bcae602d.svg?branch=trunk)](https://buildkite.com/automattic/gravatar-sdk-android)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)

Gravatar Android library

## Architecture

This project contains the source code for the Gravatar SDK library and a demo app. The demo app aims to achieve two different goals. On the one hand, it demonstrates how to use the Gravatar SDK library. On the other hand it serves as a testbed for the library.

### Gravatar SDK Architecture

The SDK is still in its early stages, but we can identify two main modules:

#### Gravatar (Core)

The core functionality of the Gravatar SDK library. It provides a set of classes and methods to interact with the Gravatar API.

- [**Gravatar API clients**](gravatar/src/main/java/com/gravatar/services): A set of classes and methods to interact with the Gravatar API. It's responsible for handling the network requests and parsing the responses.
- **Gravatar Utils**: A set of utility classes and methods to handle the Gravatar URLs, the Gravatar profile, etc.

#### Gravatar UI

A set of UI components to display the Gravatar images, profiles or information in general. Those components are implemented in Jetpack Compose and can contain logic to interact with the Gravatar API client.

- [**Gravatar UI components**](gravatar-ui/src/main/java/com/gravatar/ui): A set of UI components to display the Gravatar images, profiles or information in general. Those components are implemented in Jetpack Compose and can contain logic to interact with the Gravatar API client.

### Dependency Injection

We decided to go with a manual injection basically for two main reasons:

- We avoid adding a new project dependency and possible conflicts while integrating the SDK.
- The expected size and complexity of the SDK allow manual injection without too much trouble.

If you need to inject classes, you can use the [GravatarSdkContainer](gravatar/src/main/java/com/gravatar/di/container/GravatarSdkContainer.kt) class as the entry point. This class is responsible for all the DI in the SDK.

## Tests

Run unit tests on your machine via the following command:

```sh
./gradlew test
```

## Coding Style

We use [Ktlint](https://pinterest.github.io/ktlint) to enforce a consistent coding style. It
is integrated into the project as a Gradle plugin using
the [jlleitschuh/ktlint-gradle](https://github.com/jlleitschuh/ktlint-gradle) wrapper.

⚠️**Please make sure that _ktlintCheck_ is happy with your changes before submitting a PR.**

Check the style of the whole project or just the desired module (library or demo app) with the
following commands:

```sh
./gradlew ktlintCheck
./gradlew :gravatar:ktlintCheck
./gradlew :gravatar-ui:ktlintCheck
./gradlew :app:ktlintCheck
```

You can also try to let Ktlint fix the code style issues. Just use:

```sh
./gradlew ktlintFormat
./gradlew :gravatar:ktlintFormat
./gradlew :gravatar-ui:ktlintFormat
./gradlew :app:ktlintFormat
```

## Code static analysis

We use [Detekt](https://github.com/detekt/detekt) to perform static code analysis. You can run
Detekt via a gradle command:

```sh
./gradlew detekt
./gradlew :gravatar:detekt
./gradlew :gravatar-ui:detekt
./gradlew :app:detekt
```

## Explicit API mode

[Explicit API mode](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors) is enabled in strict mode for the
library. This means that the compiler performs additional checks that help make the library's API clearer and more consistent.

Those errors will force us to take care of the visibility modifiers in order to limit the public classes/methods of the API to the minimum
required.

By default, **Android Studio** will show a warning, `Redundant visibility modifier`.

<img width="400" alt="Redundant visibility modifier" src="docs/images/redundant_visibility_modifier_warning.png">

You can remove the warning by changing the setting for this project
in: `Settings` -> `Editor` -> `Inspections` -> `Kotlin` -> `Redundant Constructors` -> `Redundant visibility modifier`.

## Generating API code from OpenApi definitions

We use [OpenAPI Generator](https://openapi-generator.tech/) to generate the API code from the OpenAPI definitions.

The SDK project has integrated the OpenAPI Generator Gradle plugin to generate the API code from the OpenAPI definitions. The plugin is
configured in the `build.gradle.kts` file.

The OpenAPI definitions are located in the `openapi` directory. In the same directory, you can find the `templates` directory, which contains
the custom templates used by the OpenAPI Generator to generate the code that the Gravatar library needs. You can obtain the default templates by running the following command:

```sh
openapi-generator author template -g kotlin --library jvm-retrofit2
```

The [OpenAPI Generator documentation](https://openapi-generator.tech/docs/templating) provides more information about the templates.

The generator's output folder is the `build` directory. However, as we don't need all the generated files, the Gradle task has been modified to move only the desired code to the `gravatar` module. In addition, the
last step of the task is to format the generated code with [Ktlint](README.md#coding-style).

<span style="color:red">**Important:**</span> Do not manually modify the `com.gravatar.api` folder. The OpenAPI Generator will overwrite it.

To regerate the code you can use the following gradlew task:

```sh
./gradlew :gravatar:openApiGenerate
```

## Publishing

The SDK is published to the Automattic's S3 instance via [`publish-to-s3`](https://github.com/Automattic/publish-to-s3-gradle-plugin) Gradle plugin.

The published version is calculated depending on the Git context, in this order:
- if there's a tag, version is `<tag name>`
- if there's a PR, version is `<pr number>-<commit hash>`
- else `<branch name>-<commit hash>`

To use the repository, it's needed to add the custom repository:

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
    // OR
    implementation ("com.gravatar:gravatar-ui:<version>")
}
```

## Generating the API documentation

We're using [kdoc](https://kotlinlang.org/docs/kotlin-doc.html) to document the library's code. [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) has been setup to generate html documentation from kdoc. To generate the HTML docs in `docs/dokka/`, run the following command:

```sh
./gradlew dokkaHtmlMultiModule
```

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## License

Gravatar-SDK-Android is an open source project covered by the [Mozilla Public License Version 2.0](LICENSE.md).
