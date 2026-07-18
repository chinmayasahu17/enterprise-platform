# monitoring

Intentionally empty. No Prometheus or Grafana is deployed by this repo — you
own that deployment.

## Convention every service follows
- App traffic on port `8080`
- Actuator (`/actuator/health`, `/actuator/prometheus`) on port `8081`

Scrape config you write later can target port `8081` uniformly across every
service in `app-services/` and `analytics-services/`, plus `gateway`.
