#!/bin/bash -u

echo "--- 🧹 Linting"
# Run lint on app module first, to make sure that the lint-results-debug.sarif file is always generated
./gradlew :gravatar-quickeditor:lintRelease
lint_exit_code=$?

upload_sarif_to_github 'gravatar-quickeditor/build/reports/lint-results-release.sarif'

exit $lint_exit_code
