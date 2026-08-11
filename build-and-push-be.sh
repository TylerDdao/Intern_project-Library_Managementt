#!/bin/bash

DOCKERHUB_USER="tylerdao"

echo "Building backend..."
docker buildx build \
  --platform linux/amd64 \
  --no-cache \
  -t tylerdao/library-management-be:latest \
  -f be/Dockerfile.be \
  --push \
  be

if [ $? -ne 0 ]; then
    echo "Backend build failed."
    exit 1
fi

echo "Pushing backend..."
docker push "$DOCKERHUB_USER/library-management-be:latest"
  
if [ $? -ne 0 ]; then
    echo "Backend push failed."
    exit 1
fi

echo "Done. Backend image pushed to Docker Hub."
echo "Now SSH into the VPS and run: ./deploy-be.sh"
