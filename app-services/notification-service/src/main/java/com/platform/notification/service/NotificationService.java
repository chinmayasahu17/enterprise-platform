package com.platform.notification.service;

import com.platform.notification.event.ProcessingCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void send(ProcessingCompletedEvent event) {
        log.info("Simulated notification for processed record {} from source {} with status {}",
                event.id(), event.source(), event.status());
    }
}
