package com.platform.processing.controller;

import com.platform.processing.service.ProcessingService;
import com.platform.processing.repository.ProcessingRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessingControllerTest {

    private final ProcessingController controller = new ProcessingController(
            new ProcessingService(emptyRepository(), event -> { }));

    @Test
    void returnsNotFoundForUnknownRecord() {
        assertThat(controller.getRecord(UUID.randomUUID()).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private static ProcessingRecordRepository emptyRepository() {
        return (ProcessingRecordRepository) Proxy.newProxyInstance(
                ProcessingRecordRepository.class.getClassLoader(),
                new Class<?>[] { ProcessingRecordRepository.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("findById")) {
                        return Optional.empty();
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
