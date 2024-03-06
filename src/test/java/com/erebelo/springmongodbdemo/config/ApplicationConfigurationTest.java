package com.erebelo.springmongodbdemo.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationConfigurationTest {

    @Test
    void testObjectMapper() {
        var applicationConfiguration = new ApplicationConfiguration();

        var objectMapper = applicationConfiguration.objectMapper();

        assertNotNull(objectMapper);
    }
}
