#!/bin/bash -eu

echo "--- 🚀 Publishing"
./gradlew publishToMavenCentral --no-configuration-cache
