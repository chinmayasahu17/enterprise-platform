package com.platform.report.service;

import com.platform.report.event.ProcessingCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessingCompletedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProcessingCompletedEventConsumer.class);

    private final ReportService reportService;

    public ProcessingCompletedEventConsumer(ReportService reportService) {
        this.reportService = reportService;
    }

    @KafkaListener(topics = "processing.completed", groupId = "report-service-group")
    public void consume(ProcessingCompletedEvent event) {
        log.info("Received processing completion event {} for report read model", event.id());
        reportService.recordProcessedRecord(event);
    }
}
