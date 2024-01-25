# Gravatar-Android

[![Build status](https://badge.buildkite.com/8859512adb21ccf83f8f0aa03249356c6f972ff594bcae602d.svg?branch=trunk)](https://buildkite.com/automattic/gravatar-sdk-android)

Gravatar Android library

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

```
./gradlew ktlintCheck
./gradlew :gravatar:ktlintCheck
./gradlew :app:ktlintCheck
```

You can also try to let Ktlint fix the code style issues. Just use:

```
./gradlew ktlintFormat
./gradlew :gravatar:ktlintFormat
./gradlew :app:ktlintFormat
```

## Code static analysis

We use [Detekt](https://github.com/detekt/detekt) to perform static code analysis. You can run
Detekt via a gradle command:

```
./gradlew detekt
./gradlew :gravatar:detekt
./gradlew :app:detekt
```

## Publishing

The SDK is published to the Automattic's S3 instance via [`publish-to-s3`](https://github.com/Automattic/publish-to-s3-gradle-plugin) Gradle plugin.

The published version is calculated depending on the Git context, in this order:
- if there's a tag, version is <tag name>
- if there's a PR, version is <pr number>-<commit hash>
- else <branch name>-<commit hash>

To use the repository, it's needed to add the custom repository:

```groovy
repositories {
    maven {
        url "https://a8c-libs.s3.amazonaws.com/android"
    }
}

dependencies {
    implementation ("com.gravatar:gravatar:<version>")
}
```

