package com.platform.processing.controller;

import com.platform.processing.model.ProcessingRecord;
import com.platform.processing.service.ProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/processing")
public class ProcessingController {

    private final ProcessingService processingService;

    public ProcessingController(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @GetMapping("/records")
    public List<ProcessingRecord> getAllRecords() {
        return processingService.findAll();
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<ProcessingRecord> getRecord(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(processingService.findById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
