package com.erebelo.springmongodbdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.HEALTH_CHECK;

@RestController
@RequestMapping(HEALTH_CHECK)
@Tag(name = "Health Check API")
public class HealthCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

    @Operation(summary = "GET Health Check")
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getHealthCheck() {
        LOGGER.info("Getting health check");
        return ResponseEntity.ok("Spring MongoDB Demo application is up and running");
    }
}
