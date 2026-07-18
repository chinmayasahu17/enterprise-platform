# Enterprise Processing Platform — Architecture

Status: **Phase 2 complete** (platform-infra), Phase 3 in progress (app-services).

## 1. Purpose and scope

This document describes the architecture of a domain-neutral enterprise
processing platform. The platform's business logic is intentionally shallow
— it exists to provide a realistic multi-service system to design real
Kubernetes and Helm deployment artifacts against (Deployments, StatefulSets,
Services, Ingress, ConfigMaps, Secrets, PVCs, initContainers, rolling
updates, and Helm release management).

**In scope for this document:** service boundaries, communication patterns,
data ownership, resource footprint, and operational conventions.
**Out of scope:** any Kubernetes manifest, Helm chart, or CI/CD pipeline —
those are designed and built separately by the platform engineer, not
generated as part of the application codebase.

## 2. Design constraints

| Constraint | Implication |
|---|---|
| Runs on an 8GB Apple Silicon MacBook via Docker Desktop / Minikube | Every service ships with a capped JVM heap; infra is single-instance, not clustered |
| Kafka must be lightweight | KRaft mode — no Zookeeper — saves a full JVM process and 300-500MB RAM |
| No duplicate infrastructure | One Postgres, one Redis, one Kafka broker shared by all services (schema/topic isolation, not instance isolation) |
| Deployment-first design | Every service has a fixed container port, actuator port, env-var-driven config, and documented startup dependency — no service assumes co-location or shared filesystem state beyond the explicitly modeled shared volumes |

## 3. Technology stack

Java 21 · Spring Boot 3.3 · Maven · PostgreSQL 16 · Redis 7 · Kafka 3.7
(KRaft) · React · Docker (multi-stage builds, Alpine/JRE runtime images).

## 4. High-level architecture

![Architecture overview](diagrams/architecture-overview.svg)

Traffic enters through the **React UI**, which calls the **gateway** —
the platform's only public entry point. The gateway validates JWTs, applies
per-IP rate limiting (via Redis), and routes REST calls to the five
**app-services**. App-services communicate with each other and with
**analytics-services** asynchronously over Kafka. All tiers share one
Postgres instance (schema-per-service), one Redis instance, and one Kafka
broker. Note: several services also read/write Postgres and Redis directly,
not only through the tier above them — the diagram shows the two dominant
traffic paths, not every edge.

## 5. Component catalog

### 5.1 platform-infra

| Component | Role | Notes |
|---|---|---|
| gateway | Public entry point, JWT validation, per-IP rate limiting, REST routing | Only service exposed outside the cluster |
| postgres | Shared relational store | `postgres:16-alpine`, schema-per-service |
| redis | Shared cache / rate-limiter state | `redis:7-alpine`, disposable — no PVC required |
| kafka | Shared event backbone | `apache/kafka:3.7.0`, KRaft mode, single broker |
| monitoring | Placeholder only | Prometheus/Grafana deployed separately; every service exposes `/actuator/prometheus` on port 8081 for future scraping |

### 5.2 app-services

| Service | Responsibility | Reads/writes |
|---|---|---|
| ingestion-service | Accepts external data submissions, persists raw record, publishes event | Postgres (`ingestion_schema`), Kafka producer, `uploads` volume |
| processing-service | Consumes ingestion/scheduler events, applies core transformation, persists result | Postgres (`processing_schema`), Kafka producer + consumer |
| notification-service | Consumes completion events, simulates dispatch (log only, no real email/SMS) | Kafka consumer only |
| report-service | Generates reports on demand from processed/analytics data | Postgres (`report_schema`), Redis cache, `reports` volume |
| scheduler-service | Cron-style trigger for app-tier batch work | Kafka producer only |

### 5.3 analytics-services

| Service | Responsibility | Reads/writes |
|---|---|---|
| analytics-processor | Consumes platform events, computes aggregates | Postgres (`analytics_schema`), Redis counters, Kafka consumer + producer |
| analytics-scheduler | Cron-style trigger for analytics batch jobs | Kafka producer only |
| analytics-sqlclient | Read-only reporting API, queries Postgres directly | Postgres (`analytics_schema`) only — no Kafka |

## 6. Communication

- **Synchronous (REST):** UI → gateway → app-services / analytics-sqlclient. Internal service-to-service calls are avoided in favor of Kafka where the interaction is not user-request/response.
- **Asynchronous (Kafka):** all cross-service coordination that isn't a direct user request goes through named topics (catalog below). Every service that produces or consumes waits for the Kafka broker's port to accept connections before starting.

### Kafka topic catalog

| Topic | Producer | Consumer(s) |
|---|---|---|
| `ingestion.events` | ingestion-service | processing-service, analytics-processor |
| `processing.completed` | processing-service | notification-service, analytics-processor |
| `scheduler.triggers` | scheduler-service | processing-service |
| `analytics.triggers` | analytics-scheduler | analytics-processor |
| `analytics.aggregates` | analytics-processor | persisted to Postgres; read via analytics-sqlclient, not consumed from Kafka |

## 7. Data architecture

Single Postgres instance, one schema per owning service —
`ingestion_schema`, `processing_schema`, `report_schema`, `analytics_schema`
(shared by analytics-processor and analytics-sqlclient, the only two
services that share a schema, by design — they represent write and read
sides of the same domain). No service reads another service's schema
directly; cross-service data access goes through Kafka events or the
gateway's REST routes.

Redis holds only disposable state: gateway rate-limit counters,
report-service's cached report output, and analytics-processor's
fast-access aggregate counters.

## 8. Shared storage (for PV/PVC design)

| Volume | Mount path | Writer | Reader |
|---|---|---|---|
| uploads | `/data/uploads` | ingestion-service | processing-service |
| reports | `/data/reports` | report-service | analytics-sqlclient |
| config-files | `/config` (read-only) | external | gateway + all app-services |

## 9. Operational conventions

| Convention | Value |
|---|---|
| App traffic port | 8080 (every service) |
| Actuator port | 8081, separate from app traffic |
| Health endpoint | `/actuator/health` with liveness/readiness groups enabled |
| Metrics endpoint | `/actuator/prometheus` |
| Image naming | `<registry>/enterprise-platform-<service-name>:<version>` |
| Versioning | Independent per service — own Maven version, own git tag |
| JVM footprint | `-Xms128m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m`, tuned per service |
| Config | 100% environment-variable driven — no baked-in per-environment profiles |

## 10. Security model

The gateway is the only service that validates JWTs; downstream services
trust requests that reach them through it. There is no dedicated
authentication service in this platform — the gateway exposes a **dev-only**
`/api/auth/token` endpoint that issues a signed JWT with no credential
check, solely so protected routes can be exercised end-to-end during
development. This is explicitly not a production-representative auth flow
and should be called out as such if this project is ever shown outside a
learning context.

## 11. Resource footprint

9 JVM services at ~200-256MB heap each (~2GB) + Postgres (~256MB) + Redis
(~64MB) + Kafka KRaft (~512MB) ≈ 2.6-3GB running simultaneously — comfortable
on an 8GB machine with headroom for Docker Desktop/Minikube overhead,
though typical local development runs a subset of services rather than the
full platform at once.

## 12. Build status

| Phase | Scope | Status |
|---|---|---|
| 1 | Architecture, repo structure, responsibilities | Done |
| 2 | platform-infra (postgres, redis, kafka, monitoring notes; gateway app) | Done |
| 3 | app-services (ingestion, processing, notification, report, scheduler) | In progress — ingestion-service next |
| 4 | analytics-services (processor, scheduler, sqlclient) | Not started |

Deployment artifacts (Kubernetes manifests, Helm charts, CI/CD pipelines)
are out of scope for this repository's application code and are designed
separately in `deploy/`.