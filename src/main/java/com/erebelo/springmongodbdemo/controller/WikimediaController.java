package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.WIKIMEDIA;

@Log4j2
@RestController
@RequestMapping(WIKIMEDIA)
@RequiredArgsConstructor
@Tag(name = "Wikimedia API")
public class WikimediaController {

    private final WikimediaService service;

    @Operation(summary = "GET Wikimedia project pageviews")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WikimediaResponse> getWikimediaProjectPageviews() {
        log.info("Getting Wikimedia project pageviews");
        var response = service.getWikimediaProjectPageviews();

        return ResponseEntity.ok(response);
    }
}
