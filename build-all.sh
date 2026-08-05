#!/usr/bin/env bash

# Build and publish multi-architecture manifests for every custom service.
set -Eeuo pipefail

readonly BUILDER_NAME="enterprise-platform-multiarch"
readonly PLATFORMS="linux/amd64,linux/arm64"
readonly REGISTRY="chinmaya1"

services=(
  "gateway|platform-infra/gateway|enterprise-platform-gateway|1.0.1"
  "ingestion|app-services/ingestion-service|enterprise-platform-ingestion|1.0.1"
  "processing|app-services/processing-service|enterprise-platform-processing|1.0.1"
  "notification|app-services/notification-service|enterprise-platform-notification|1.0.1"
  "scheduler|app-services/scheduler-service|enterprise-platform-scheduler|1.0.1"
  "report|app-services/report-service|enterprise-platform-report|1.0.2"
  "analytics-processor|analytics-services/analytics-processor|enterprise-platform-analytics-processor|0.1.0"
  "analytics-scheduler|analytics-services/analytics-scheduler|enterprise-platform-analytics-scheduler|0.1.0"
  "analytics-sqlclient|analytics-services/analytics-sqlclient|enterprise-platform-analytics-sqlclient|0.1.0"
)

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker is required but was not found in PATH." >&2
  exit 1
fi

echo "==> Preparing Docker buildx builder: ${BUILDER_NAME}"
if docker buildx inspect "${BUILDER_NAME}" >/dev/null 2>&1; then
  docker buildx use "${BUILDER_NAME}"
else
  docker buildx create --name "${BUILDER_NAME}" --driver docker-container --use
fi
docker buildx inspect --bootstrap "${BUILDER_NAME}"

for service in "${services[@]}"; do
  IFS='|' read -r name context image tag <<< "${service}"
  full_image="${REGISTRY}/${image}:${tag}"

  echo
  echo "==> Building and pushing ${name}: ${full_image} (${PLATFORMS})"
  docker buildx build \
    --platform "${PLATFORMS}" \
    --tag "${full_image}" \
    --push \
    "${context}"
  echo "==> Completed ${name}"
done

echo
echo "==> All multi-architecture images were built and pushed successfully."
