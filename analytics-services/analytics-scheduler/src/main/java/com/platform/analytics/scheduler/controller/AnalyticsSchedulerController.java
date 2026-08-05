package com.platform.analytics.scheduler.controller;

import com.platform.analytics.scheduler.service.AnalyticsSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/jobs")
public class AnalyticsSchedulerController {
    private final AnalyticsSchedulerService analyticsSchedulerService;
    public AnalyticsSchedulerController(AnalyticsSchedulerService analyticsSchedulerService) { this.analyticsSchedulerService = analyticsSchedulerService; }
    @GetMapping
    public List<String> getJobs() { return analyticsSchedulerService.getJobs(); }
    @PostMapping("/{job}/trigger")
    public ResponseEntity<?> trigger(@PathVariable String job) {
        try { return ResponseEntity.ok(analyticsSchedulerService.trigger(job)); }
        catch (IllegalArgumentException ex) { return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage())); }
    }
}
