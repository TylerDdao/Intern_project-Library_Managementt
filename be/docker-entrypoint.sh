#!/bin/sh
set -e

# Ensure mounted volume directories exist and are owned by the app user,
# since fresh named volumes are created root-owned by default, which
# conflicts with the container running as the non-root "spring" user.
mkdir -p /app/logs /app/uploads/book-covers
chown -R spring:spring /app/logs /app/uploads

exec su-exec spring java -jar app.jar
