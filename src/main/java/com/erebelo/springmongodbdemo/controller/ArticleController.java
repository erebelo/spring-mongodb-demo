package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ARTICLES_PATH;

import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import com.erebelo.springmongodbdemo.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(ARTICLES_PATH)
@RequiredArgsConstructor
@Tag(name = "Articles API")
public class ArticleController {

    private final ArticleService service;

    @Operation(summary = "GET Articles")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleDataResponseDTO>> getArticles() {
        log.info("GET {}", ARTICLES_PATH);
        List<ArticleDataResponseDTO> response = service.getArticles();

        return ResponseEntity.ok(response);
    }
}
