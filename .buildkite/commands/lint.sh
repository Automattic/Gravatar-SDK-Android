#!/bin/bash -u

echo "--- 🧹 Linting"
./gradlew :demo-app:lintRelease
lint_exit_code=$?

upload_sarif_to_github 'demo-app/build/reports/lint-results-release.sarif'

exit $lint_exit_code
