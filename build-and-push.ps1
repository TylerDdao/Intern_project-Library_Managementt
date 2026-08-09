# Run this from the repo root on your local Windows machine (PowerShell)
# Builds both images fresh (no cache) and pushes them to Docker Hub

$DOCKERHUB_USER = "tylerdao"

Write-Host "Building backend..." -ForegroundColor Cyan
docker build --no-cache -t "$DOCKERHUB_USER/library-management-be:latest" -f be/Dockerfile.be be
if ($LASTEXITCODE -ne 0) { Write-Host "Backend build failed." -ForegroundColor Red; exit 1 }

Write-Host "Building frontend..." -ForegroundColor Cyan
docker build --no-cache -t "$DOCKERHUB_USER/library-management-fe:latest" -f fe/Dockerfile.fe fe
if ($LASTEXITCODE -ne 0) { Write-Host "Frontend build failed." -ForegroundColor Red; exit 1 }

Write-Host "Pushing backend..." -ForegroundColor Cyan
docker push "$DOCKERHUB_USER/library-management-be:latest"
if ($LASTEXITCODE -ne 0) { Write-Host "Backend push failed." -ForegroundColor Red; exit 1 }

Write-Host "Pushing frontend..." -ForegroundColor Cyan
docker push "$DOCKERHUB_USER/library-management-fe:latest"
if ($LASTEXITCODE -ne 0) { Write-Host "Frontend push failed." -ForegroundColor Red; exit 1 }

Write-Host "Done. Both images pushed to Docker Hub." -ForegroundColor Green
Write-Host "Now SSH into the VPS and run: bash deploy.sh" -ForegroundColor Yellow
