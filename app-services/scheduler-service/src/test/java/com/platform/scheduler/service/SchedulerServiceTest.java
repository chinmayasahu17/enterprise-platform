package com.platform.scheduler.service;

import com.platform.scheduler.event.SchedulerTriggerEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SchedulerServiceTest {

    private final List<SchedulerTriggerEvent> publishedEvents = new ArrayList<>();
    private final SchedulerService schedulerService = new SchedulerService(publishedEvents::add);

    @Test
    void publishesProcessingTrigger() {
        SchedulerTriggerEvent event = schedulerService.trigger("processing");

        assertThat(event.job()).isEqualTo("processing");
        assertThat(publishedEvents).containsExactly(event);
    }

    @Test
    void rejectsUnknownJob() {
        assertThatThrownBy(() -> schedulerService.trigger("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("unknown job: unknown");
    }
}
