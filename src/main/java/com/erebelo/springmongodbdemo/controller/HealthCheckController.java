package com.erebelo.springmongodbdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.HEALTH_CHECK;

@RestController
@RequestMapping(HEALTH_CHECK)
@Tag(name = "Health Check API")
public class HealthCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

    @Operation(summary = "GET health check api")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHealthCheck() {
        LOGGER.info("Getting health check");
        return new ResponseEntity<>("Spring MongoDB Demo application is up and running", HttpStatus.OK);
    }
}
