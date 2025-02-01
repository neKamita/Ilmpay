#!/bin/bash

# AWS Credentials
export AWS_ACCESS_KEY="AKIA36E76CYUBBMMYYNG"
export AWS_SECRET_KEY="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"
export AWS_REGION="eu-north-1"
export AWS_S3_BUCKET="ilmpay"

# Print the exported variables (masked for security)
echo "AWS environment variables set:"
echo "AWS_ACCESS_KEY=${AWS_ACCESS_KEY:0:8}..."
echo "AWS_SECRET_KEY=${AWS_SECRET_KEY:0:8}..."
echo "AWS_REGION=$AWS_REGION"
echo "AWS_S3_BUCKET=$AWS_S3_BUCKET"
