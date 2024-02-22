package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.ARTICLES;

@RestController
@RequestMapping(ARTICLES)
@RequiredArgsConstructor
@Tag(name = "Articles API")
public class ArticlesController {

    private final ArticlesService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticlesController.class);
    private static final String ARTICLES_RESPONSE = "Articles response: {}";

    @Operation(summary = "GET Articles")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticlesDataResponseDTO>> getArticles() {
        LOGGER.info("Getting articles");
        var response = service.getArticles();

        LOGGER.info(ARTICLES_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

