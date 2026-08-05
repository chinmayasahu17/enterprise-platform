package com.platform.scheduler.service;

import com.platform.scheduler.event.SchedulerTriggerEvent;

public interface SchedulerEventPublisher {

    void publish(SchedulerTriggerEvent event);
}
