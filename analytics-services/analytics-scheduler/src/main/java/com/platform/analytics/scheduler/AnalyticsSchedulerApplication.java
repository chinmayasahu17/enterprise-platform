package com.platform.analytics.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnalyticsSchedulerApplication {
    public static void main(String[] args) { SpringApplication.run(AnalyticsSchedulerApplication.class, args); }
}
