package com.platform.analytics.scheduler.service;

import com.platform.analytics.scheduler.event.AnalyticsTriggerEvent;

public interface AnalyticsTriggerPublisher { void publish(AnalyticsTriggerEvent event); }
