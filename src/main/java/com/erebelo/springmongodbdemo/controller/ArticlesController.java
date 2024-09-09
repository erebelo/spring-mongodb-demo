package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ARTICLES;

@Log4j2
@RestController
@RequestMapping(ARTICLES)
@RequiredArgsConstructor
@Tag(name = "Articles API")
public class ArticlesController {

    private final ArticlesService service;

    @Operation(summary = "GET Articles")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticlesDataResponseDTO>> getArticles() {
        log.info("Getting articles");
        var response = service.getArticles();

        return ResponseEntity.ok(response);
    }
}

