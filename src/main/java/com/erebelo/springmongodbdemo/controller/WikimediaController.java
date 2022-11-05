package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.WIKIMEDIA;

@RestController
@Tag(name = "Wikimedia API")
public class WikimediaController {

    @Autowired
    private WikimediaService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaController.class);
    private static final String WIKIMEDIA_RESPONSE = "Wikimedia response: {}";

    @Operation(summary = "GET Wikimedia project pageviews")
    @GetMapping(value = WIKIMEDIA, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WikimediaResponse> getWikimediaProjectPageviews() {
        LOGGER.info("Getting Wikimedia project pageviews");
        var response = service.getWikimediaProjectPageviews();

        LOGGER.info(WIKIMEDIA_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
