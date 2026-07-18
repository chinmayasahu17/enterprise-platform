package com.platform.notification.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationControllerTest {

    private final NotificationController controller = new NotificationController();

    @Test
    void reportsServiceAsUp() {
        assertThat(controller.health()).containsEntry("status", "UP");
    }
}
