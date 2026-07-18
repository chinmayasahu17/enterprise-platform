# postgres

Shared instance used by every service that needs durable storage. One
StatefulSet, one PVC, schema-per-service — not a database per service.

## Image
`postgres:16-alpine` — small footprint, matches production-grade major version.

## Suggested sizing (8GB dev machine)
- Memory: 256Mi request / 384Mi limit
- Storage: 2Gi PVC is more than enough for this project's data volume
- `shared_buffers` ~64MB, `max_connections` ~50 (9 services × a handful of pooled connections each, comfortably under default 100)

## Schemas (created by each service on first startup, or via init script — your call)
| Schema | Owner service |
|---|---|
| `ingestion_schema` | ingestion-service |
| `processing_schema` | processing-service |
| `report_schema` | report-service |
| `analytics_schema` | analytics-processor, analytics-sqlclient |

## Environment variables consuming services expect to set
```
DB_HOST=postgres          # k8s Service DNS name
DB_PORT=5432
DB_NAME=platform
DB_USER=<service-specific or shared app user>
DB_PASSWORD=<from Secret>
DB_SCHEMA=<service's schema, e.g. ingestion_schema>
```

## Startup ordering note
Every service that touches Postgres should wait for `pg_isready` on
`postgres:5432` before starting — good candidate for your first initContainer.

## Not included here
No StatefulSet, PVC, Service, ConfigMap, or Secret — those are yours to build.
