#!/bin/bash -eu

# The A8C_LIBS_* credentials are for the `a8c-libs` S3 bucket in which we want to publish the SDK artifact,
# and which lives in a separate `a8c` AWS account.
# We don't use the standard AWS env names here to prevent conflicts with the AWS credentials
# of our main `a8c-apps` account (used for all our other AppsInfra S3 buckets).

export AWS_ACCESS_KEY="${A8C_LIBS_AWS_ACCESS_KEY}"
export AWS_SECRET_KEY="${A8C_LIBS_AWS_SECRET_KEY}"

echo "--- 🚀 Publishing"
./gradlew \
:gravatar:prepareToPublishToS3 "$(prepare_to_publish_to_s3_params)" \
:gravatar:publish \
:gravatar-ui:prepareToPublishToS3 "$(prepare_to_publish_to_s3_params)" \
:gravatar-ui:publish \
:gravatar-quickeditor:prepareToPublishToS3 "$(prepare_to_publish_to_s3_params)" \
:gravatar-quickeditor:publish \
--no-configuration-cache
