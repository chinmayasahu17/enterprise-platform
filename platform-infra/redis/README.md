# redis

Shared cache/rate-limiter store. One instance, no replication needed for this
project's scale.

## Image
`redis:7-alpine`

## Suggested sizing (8GB dev machine)
- Memory: 64Mi request / 128Mi limit, `--maxmemory 96mb --maxmemory-policy allkeys-lru`
- No PVC required unless you want to practice persistence — Redis here is
  disposable cache/rate-limit state, safe to lose on restart

## Usage across the platform
| Consumer | Uses Redis for |
|---|---|
| gateway | Request rate limiting (RedisRateLimiter) |
| report-service | Caching generated report results |
| analytics-processor | Fast-access aggregate counters |

## Environment variables consuming services expect to set
```
REDIS_HOST=redis          # k8s Service DNS name
REDIS_PORT=6379
```
No auth configured by default — if you want to practice Secret-mounted Redis
passwords, set `requirepass` and add `REDIS_PASSWORD` to the consumer list
above when you get there.

## Startup ordering note
gateway specifically should not start serving traffic until Redis is
reachable (rate limiter fails closed). Other services treat Redis as
best-effort cache and can start without it.

## Not included here
No Deployment, Service, ConfigMap, or Secret — those are yours to build.
