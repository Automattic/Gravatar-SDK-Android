#!/bin/bash -u

echo "--- 🧹 Linting"
# Run lint on the app module first to ensure that the SARIF file is always generated.
./gradlew :gravatar-quickeditor:lintRelease
lint_exit_code=$?

upload_sarif_to_github 'gravatar-quickeditor/build/reports/lint-results-release.sarif'

exit $lint_exit_code
