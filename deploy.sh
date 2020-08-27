#!/bin/bash

S3_BUCKET=todo-list-aws-lambda
INPUT_FILE=sam-template.yaml
OUTPUT_FILE=sam-template-output.yaml
STACK_NAME=todo-list-aws-lambda-api

mvn clean package -DskipTests

aws cloudformation package --template-file $INPUT_FILE --output-template-file $OUTPUT_FILE --s3-bucket $S3_BUCKET
aws cloudformation deploy --template-file $OUTPUT_FILE --stack-name $STACK_NAME --capabilities CAPABILITY_IAM
aws cloudformation describe-stacks --stack-name $STACK_NAME



