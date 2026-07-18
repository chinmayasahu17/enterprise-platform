# gateway

## Purpose
Single entry point for the platform. Routes REST traffic to app-services and
analytics-sqlclient, validates JWTs on every non-public route, and rate-limits
per client IP using Redis. No business logic lives here.

## APIs
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/token` | none | **Dev-only.** Issues a JWT for testing protected routes. No credential check. |
| ANY | `/api/ingestion/**` | Bearer JWT | Proxied to ingestion-service |
| ANY | `/api/processing/**` | Bearer JWT | Proxied to processing-service |
| ANY | `/api/notifications/**` | Bearer JWT | Proxied to notification-service |
| ANY | `/api/reports/**` | Bearer JWT | Proxied to report-service |
| ANY | `/api/analytics/**` | Bearer JWT | Proxied to analytics-sqlclient |
| GET | `/actuator/health` | none | Liveness/readiness, see below |
| GET | `/actuator/prometheus` | none | Metrics scrape endpoint |

## Dependencies
- Redis (rate limiter state)
- Postgres: **none** — gateway holds no data of its own
- All five routed services, for actual traffic (not required at startup)

## Exposed ports
- `8080` — application traffic
- `8081` — actuator (health + prometheus), separate from app traffic by design

## Required environment variables
See `.env.example` for the full list and defaults. Notable ones:
- `JWT_SECRET` — HMAC signing key, must match across restarts or existing tokens break
- `REDIS_HOST` / `REDIS_PORT`
- `*_SERVICE_URL` — one per routed service; in Kubernetes set these to the ClusterIP Service DNS name

## Startup dependencies
Needs **Redis** reachable at boot (rate limiter fails closed otherwise).
Does **not** need the downstream services to be up at startup — Spring Cloud
Gateway routes lazily, so a 502/503 on a given route just means that
particular backend isn't ready yet. Useful to know when you design your
initContainers: only gate gateway's startup on Redis, not on every service.

## Health & metrics
- `GET /actuator/health` — includes liveness/readiness groups (`/actuator/health/liveness`, `/actuator/health/readiness`), useful for separate livenessProbe/readinessProbe config later
- `GET /actuator/prometheus` — Prometheus text format on port 8081

## Build & run locally
```bash
# Build
mvn clean package

# Run the jar directly
JWT_SECRET=dev-secret REDIS_HOST=localhost java -jar target/gateway.jar

# Build the image
docker build -t enterprise-platform-gateway:0.1.0 .

# Run the container (needs a Redis reachable at REDIS_HOST)
docker run --rm -p 8080:8080 -p 8081:8081 \
  --env-file .env \
  enterprise-platform-gateway:0.1.0
```

## Image naming & versioning
- Image: `<your-registry>/enterprise-platform-gateway:<version>`
- This service versions independently — bump `<version>` in `pom.xml` and tag
  `gateway/v0.1.0` when you cut a release, separate from other services' tags.

## Quick end-to-end check once other services exist
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/token \
  -H 'Content-Type: application/json' -d '{"subject":"me"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/ingestion/health
```
