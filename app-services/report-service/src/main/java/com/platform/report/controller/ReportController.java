package com.platform.report.controller;

import com.platform.report.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<ReportSummary> getAllReports() {
        return reportService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(reportService.findById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody(required = false) ReportGenerationRequest request) {
        ReportGenerationRequest reportRequest = request == null ? new ReportGenerationRequest(null, null) : request;
        if (reportRequest.from() != null && reportRequest.to() != null
                && reportRequest.from().isAfter(reportRequest.to())) {
            return ResponseEntity.badRequest().body(Map.of("error", "from must not be after to"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.generate(reportRequest));
    }
}
