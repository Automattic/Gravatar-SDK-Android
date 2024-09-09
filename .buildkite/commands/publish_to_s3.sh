#!/bin/bash -eu

echo "--- 🚀 Publishing"
./gradlew \
:gravatar:prepareToPublishToS3 $(prepare_to_publish_to_s3_params) \
:gravatar:publish \
:gravatar-ui:prepareToPublishToS3 $(prepare_to_publish_to_s3_params) \
:gravatar-ui:publish \
:gravatar-quickeditor:prepareToPublishToS3 $(prepare_to_publish_to_s3_params) \
:gravatar-quickeditor:publish \
--no-configuration-cache
