package com.platform.analytics.sqlclient.controller;

import com.platform.analytics.sqlclient.service.AnalyticsQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsQueryService analyticsQueryService;
    public AnalyticsController(AnalyticsQueryService analyticsQueryService) { this.analyticsQueryService = analyticsQueryService; }
    @GetMapping("/summary")
    public AnalyticsSummaryResponse summary() { return analyticsQueryService.summary(); }
    @GetMapping("/daily")
    public List<AnalyticsDailyResponse> daily() { return analyticsQueryService.daily(); }
    @GetMapping("/source/{source}")
    public AnalyticsSourceResponse source(@PathVariable String source) { return analyticsQueryService.source(source); }
}
