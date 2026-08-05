package com.platform.analytics.processor.controller;

import com.platform.analytics.processor.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    public AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }
    @GetMapping("/status")
    public AnalyticsService.AnalyticsStatus status() { return analyticsService.status(); }
}
