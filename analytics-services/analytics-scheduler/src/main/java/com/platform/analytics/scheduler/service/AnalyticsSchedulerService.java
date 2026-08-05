package com.platform.analytics.scheduler.service;

import com.platform.analytics.scheduler.event.AnalyticsTriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AnalyticsSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsSchedulerService.class);
    private static final String ANALYTICS_JOB = "analytics";
    private final AnalyticsTriggerPublisher analyticsTriggerPublisher;
    public AnalyticsSchedulerService(AnalyticsTriggerPublisher analyticsTriggerPublisher) { this.analyticsTriggerPublisher = analyticsTriggerPublisher; }
    @Scheduled(cron = "${platform.scheduler.analytics-cron}")
    public void triggerAnalyticsJob() { trigger(ANALYTICS_JOB); }
    public AnalyticsTriggerEvent trigger(String job) {
        if (!ANALYTICS_JOB.equals(job)) { throw new IllegalArgumentException("unknown job: " + job); }
        AnalyticsTriggerEvent event = new AnalyticsTriggerEvent(UUID.randomUUID(), job, Instant.now());
        analyticsTriggerPublisher.publish(event);
        log.info("Published analytics trigger {} for job {}", event.id(), event.job());
        return event;
    }
    public List<String> getJobs() { return List.of(ANALYTICS_JOB); }
}
