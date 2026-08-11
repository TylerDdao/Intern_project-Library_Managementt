#!/bin/bash

DOCKERHUB_USER="tylerdao"

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

echo "Pushing frontend..."
docker push "$DOCKERHUB_USER/library-management-fe:latest"
    
if [ $? -ne 0 ]; then
    echo "Frontend push failed."
    exit 1
fi
  
echo "Done. Frontend image pushed to Docker Hub."
echo "Now SSH into the VPS and run: ./deploy-fe.sh"
