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

echo "Building frontend..."
docker buildx build \
  --platform linux/amd64 \
  --no-cache \
  -t tylerdao/library-management-fe:latest \
  -f fe/Dockerfile.fe \
  --push \
  fe

if [ $? -ne 0 ]; then
    echo "Frontend build failed."
    exit 1
fi

echo "Pushing backend..."
docker push "$DOCKERHUB_USER/library-management-be:latest"

if [ $? -ne 0 ]; then
    echo "Backend push failed."
    exit 1
fi

echo "Pushing frontend..."
docker push "$DOCKERHUB_USER/library-management-fe:latest"

if [ $? -ne 0 ]; then
    echo "Frontend push failed."
    exit 1
fi

echo "Done. Both images pushed to Docker Hub."
echo "Now SSH into the VPS and run: bash deploy.sh"
