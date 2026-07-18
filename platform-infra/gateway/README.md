# gateway

## Purpose
Single entry point for the platform. Routes REST traffic to app-services and
analytics-sqlclient, validates JWTs on every non-public route, and rate-limits
per client IP using Redis. No business logic lives here.

## APIs

| Method | Path                    | Auth       | Description                                                                   |
| ------ | ----------------------- | ---------- | ----------------------------------------------------------------------------- |
| POST   | `/api/auth/token`       | none       | **Dev-only.** Issues a JWT for testing protected routes. No credential check. |
| ANY    | `/api/ingestion/**`     | Bearer JWT | Proxied to ingestion-service                           |
| ANY    | `/api/processing/**`    | Bearer JWT | Proxied to processing-service                          |
| ANY    | `/api/notifications/**` | Bearer JWT | Proxied to notification-service                        |
| ANY    | `/api/reports/**`       | Bearer JWT | Proxied to report-service                              |
| ANY    | `/api/analytics/**`     | Bearer JWT | Proxied to analytics-sqlclient                         |
| GET    | `/actuator/health`      | none       | Liveness/readiness, see below                          |
| GET    | `/actuator/prometheus`  | none       | Metrics scrape endpoint                                |

## Dependencies

- Redis (rate limiter state)
- Postgres: **none** — gateway holds no data of its own
- All routed services, for actual traffic (not required at startup)

## Exposed ports

- `8080` — application traffic
- `8081` — actuator (health + prometheus)

## Required environment variables

See `.env.example` for the full list and defaults.

Important variables:

- `JWT_SECRET` — HMAC signing key.
- `REDIS_HOST` / `REDIS_PORT`
- `INGESTION_SERVICE_URL`
- `PROCESSING_SERVICE_URL`
- `NOTIFICATION_SERVICE_URL`
- `REPORT_SERVICE_URL`
- `ANALYTICS_SQLCLIENT_URL`

The service URLs are environment-specific:

- **Local development:** `http://localhost:<port>`
- **Docker Compose:** `http://<container-name>:8080`
- **Kubernetes:** `http://<service-name>:8080`

## Startup dependencies

Gateway requires **Redis** to be reachable during startup because the
`RequestRateLimiter` filter stores rate-limiting state in Redis.

Downstream services (Ingestion, Processing, Notification, etc.) are **not**
required during startup. Routes are resolved lazily, so requests will fail only
for services that are unavailable.

## Health & Metrics

- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Build & Run

### Build

```bash
mvn clean package
```

### Run locally

```bash
JWT_SECRET=dev-secret REDIS_HOST=localhost java -jar target/gateway.jar
```

### Build Docker image

```bash
docker build -t enterprise-platform-gateway:0.1.0 .
```

### Run Docker container

```bash
docker run --rm \
  -p 8080:8080 \
  -p 8081:8081 \
  --network platform-network \
  --env-file .env \
  enterprise-platform-gateway:0.1.0
```

> **Note:** When running inside Docker, configure the `*_SERVICE_URL`
> environment variables to use Docker container names (for example,
> `http://ingestion:8080`) instead of `localhost`.

## Image naming & versioning

- Image: `<registry>/enterprise-platform-gateway:<version>`
- Version independently from other services.

## Quick end-to-end check

Generate a JWT:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/token \
-H "Content-Type: application/json" \
-d '{"subject":"me"}' | jq -r .token)
```

Call the Gateway:

```bash
curl -X POST http://localhost:8080/api/ingestion/records \
-H "Authorization: Bearer $TOKEN" \
-H "Content-Type: application/json" \
-d '{"source":"gateway","payload":"Hello Gateway"}'
```