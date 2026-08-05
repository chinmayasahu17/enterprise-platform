package com.platform.scheduler.service;

import com.platform.scheduler.event.SchedulerTriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private static final String PROCESSING_JOB = "processing";

    private final SchedulerEventPublisher schedulerEventPublisher;

    public SchedulerService(SchedulerEventPublisher schedulerEventPublisher) {
        this.schedulerEventPublisher = schedulerEventPublisher;
    }

    @Scheduled(cron = "${platform.scheduler.processing-cron}")
    public void triggerProcessingJob() {
        trigger(PROCESSING_JOB);
    }

    public SchedulerTriggerEvent trigger(String job) {
        if (!PROCESSING_JOB.equals(job)) {
            throw new IllegalArgumentException("unknown job: " + job);
        }

        SchedulerTriggerEvent event = new SchedulerTriggerEvent(UUID.randomUUID(), job, Instant.now());
        schedulerEventPublisher.publish(event);
        log.info("Published scheduler trigger {} for job {}", event.id(), event.job());
        return event;
    }

    public List<String> getJobs() {
        return List.of(PROCESSING_JOB);
    }
}
