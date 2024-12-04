# Documentation Overview

**[View the API Documentation](https://automattic.github.io/Gravatar-SDK-Android/current/index.html)**

This branch contains the generated API documentation for the project.

## How the Documentation is Generated

1. **Tool Used**:  
   The documentation is generated using [Dokka](https://kotlinlang.org/docs/dokka-overview.html).

2. **Trigger**:  
   Documentation is generated automatically by a GitHub Actions workflow whenever a new version tag (e.g., `1.0.0`) is pushed to the repository.

3. **Process**:
    - `docs` branch is checked out to `./docs` directory.
    - `{latest_version}` tag is checked out to `./code` directory.
    - Both `./docs/history` and `./docs/current` are copied to `./code/docs/dokka` directory. This is required to preserve historical documentation.
    - Dokka is executed to generate HTML documentation in the `./code/docs/dokka` directory.
    - Both subdirectories from `./code/docs/dokka/` are copied to the root of the `./docs` directory.
    - The changes in `./docs` are committed and pushed to the `docs` branch.

4. **Versioned History**:  
   Historical documentation is preserved for each release under `history/<version>`. This allows users to browse API documentation for any version of the project.

