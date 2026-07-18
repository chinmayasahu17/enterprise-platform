package com.platform.processing.service;

import com.platform.processing.event.ProcessingCompletedEvent;

public interface ProcessingEventPublisher {

    void publish(ProcessingCompletedEvent event);
}
