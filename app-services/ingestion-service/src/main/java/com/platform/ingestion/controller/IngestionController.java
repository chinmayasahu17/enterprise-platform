package com.platform.ingestion.controller;

import com.platform.ingestion.service.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/records")
    public ResponseEntity<?> submit(@RequestBody SubmissionRequest request) {
        if (request == null || request.payload() == null || request.payload().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "payload must not be blank"));
        }

        SubmissionResponse response = ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
