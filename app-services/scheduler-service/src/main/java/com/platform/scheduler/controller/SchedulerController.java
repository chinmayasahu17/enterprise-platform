package com.platform.scheduler.controller;

import com.platform.scheduler.service.SchedulerService;
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
public class SchedulerController {

    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping
    public List<String> getJobs() {
        return schedulerService.getJobs();
    }

    @PostMapping("/{job}/trigger")
    public ResponseEntity<?> trigger(@PathVariable String job) {
        try {
            return ResponseEntity.ok(schedulerService.trigger(job));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
