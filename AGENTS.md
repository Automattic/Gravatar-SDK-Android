# AGENTS.md

This file provides guidance to coding agents when working with code in this repository.

## Project Overview

Android SDK for Gravatar — provides avatar URL generation, profile fetching/updating, avatar management, and ready-made Compose UI components (profile cards, Quick Editor). Published to Maven Central.

## Modules

- **gravatar** — Core SDK: avatar URLs, REST API client (OpenAPI-generated), profile/avatar services, OkHttp networking with Moshi serialization. See [Core Module Usage](docs/get-started/usage-core.md).
- **gravatar-ui** — Compose UI components: profile cards, avatar views (depends on `gravatar`). See [UI Module Usage](docs/get-started/usage-ui.md).
- **gravatar-quickeditor** — Drop-in Compose profile editor with OAuth, image cropping, avatar selection (depends on `gravatar` + `gravatar-ui`). See [Quick Editor Module Usage](docs/get-started/usage-quickeditor.md).
- **demo-app** — Sample application showcasing SDK features
- **uitestutils** — Shared test utilities for screenshot tests

For installation, API key setup, and general usage, see [Get Started](docs/get-started/get-started.md).

## Build Commands

```bash
./gradlew assembleDebug                    # Build all modules
./gradlew testDebugUnitTest                # All unit tests
./gradlew lintDebug                        # Android lint
./gradlew detekt                           # Static analysis (excludes generated restapi code)
./gradlew ktlintCheck                      # Code formatting check
./gradlew ktlintFormat                     # Auto-fix formatting issues
./gradlew apiCheck                         # Binary compatibility validation
./gradlew apiDump                          # Generate/update .api files after public API changes
```

Screenshot tests (Roborazzi):
```bash
./gradlew :gravatar-ui:verifyRoborazziDebug -Pscreenshot
./gradlew :gravatar-quickeditor:verifyRoborazziDebug -Pscreenshot
./gradlew :gravatar-ui:recordRoborazziDebug -Pscreenshot         # Record new baselines
./gradlew :gravatar-quickeditor:recordRoborazziDebug -Pscreenshot
```

Run a single test class:
```bash
./gradlew :gravatar:testDebugUnitTest --tests "com.gravatar.AvatarUrlTest"
```

OpenAPI code generation:
```bash
./gradlew :gravatar:openApiGenerate
```

## Architecture

For an in-depth overview of the SDK's inner workings, design decisions, and implementation details, see [SDK Technical Details](docs/sdk-technical-details/sdk-technical-details.md).

### Build System

Composite build with convention plugins in `build-logic/`:
- `gravatar.android.library` — shared Android library config (min/target SDK, Kotlin, detekt, ktlint)
- `gravatar.android.compose` — Compose compiler and Roborazzi setup
- `gravatar.openapi.generator` — OpenAPI code generation for REST API models/clients
- `gravatar.maven.publish` — Maven Central publishing via Vanniktech plugin

### REST API Layer

The `gravatar/src/main/java/com/gravatar/restapi/` package is **auto-generated** from an OpenAPI spec via `openApiGenerate`. Do not edit these files manually. Key generated components:
- `apis/ProfilesApi`, `apis/AvatarsApi` — API endpoint definitions
- `models/` — Data models (Profile, Avatar, etc.)
- `infrastructure/` — HTTP client infrastructure (ApiClient, Serializer)

### Service Layer

Hand-written service classes in `gravatar/src/main/java/com/gravatar/services/` wrap the generated API:
- `ProfileService` — Profile fetching and updating
- `AvatarService` — Avatar upload and management
- `GravatarResult` — Sealed result type for API responses

### SDK Initialization

`Gravatar.kt` is the entry point — configures API key and OkHttp client. Uses `GravatarSdkContainer` for [manual dependency injection](docs/sdk-technical-details/sdk-technical-details.md#dependency-injection).

### Versioning

SDK version is derived from `git describe --tags`. Non-tag builds append `-SNAPSHOT`. The version is injected as `BuildConfig.SDK_VERSION` in the `gravatar` module.

## Testing

- JUnit 4 + Robolectric for unit tests needing Android resources
- MockK for mocking
- Turbine for Flow testing
- MockWebServer for HTTP testing
- Roborazzi for screenshot/snapshot tests (modules: `gravatar-ui`, `gravatar-quickeditor`)
- Unit tests use `kotlin.test` assertions (`assertEquals`, `assertNotNull`, etc.) — **not** AssertJ
- Test names use backtick format with descriptive names

## Important Gotchas

- **Generated code is checked in**: The `restapi/` package under `gravatar` is committed to the repo. Run `openApiGenerate` to regenerate, but don't hand-edit those files. See [OpenAPI generation](docs/sdk-technical-details/sdk-technical-details.md#generating-api-code-from-openapi-definitions) for details on templates and customization.
- **Screenshot test baselines**: Must pass `-Pscreenshot` flag for Roborazzi tasks. Baselines live in `gravatar-ui/screenshotTests/roborazzi/` and `gravatar-quickeditor/screenshotTests/roborazzi/`.
- **Binary compatibility**: Public API changes require running `./gradlew apiDump` to update `.api` files. CI enforces compatibility via `apiCheck`.
- **Explicit API mode**: Enabled in strict mode for library modules — all public declarations require explicit visibility modifiers. See [Explicit API mode](docs/sdk-technical-details/sdk-technical-details.md#explicit-api-mode).
- **`secrets.properties`**: Required for the demo app but managed via `.configure` — never commit actual secrets.
