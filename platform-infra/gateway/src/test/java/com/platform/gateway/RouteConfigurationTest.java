package com.platform.gateway;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteConfigurationTest {

    @Test
    void ingestionRouteDefaultsToTheIngestionServicePort() throws IOException {
        String applicationYaml = Files.readString(Path.of("src/main/resources/application.yml"));

        assertTrue(applicationYaml.contains("- id: ingestion-service"));
        assertTrue(applicationYaml.contains("uri: ${INGESTION_SERVICE_URL:http://localhost:8180}"));
    }
}
