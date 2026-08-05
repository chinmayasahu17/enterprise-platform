package com.platform.scheduler.controller;

import com.platform.scheduler.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerControllerTest {

    private final SchedulerController controller = new SchedulerController(new SchedulerService(event -> { }));

    @Test
    void listsProcessingJob() {
        assertThat(controller.getJobs()).containsExactly("processing");
    }

    @Test
    void rejectsUnknownJob() {
        assertThat(controller.trigger("unknown").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
