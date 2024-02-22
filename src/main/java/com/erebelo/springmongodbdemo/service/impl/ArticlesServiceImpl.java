package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.rest.HttpClient;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.utils.AuthenticationUtils.getBasicHttpHeaders;

@Service
@RequiredArgsConstructor
public class ArticlesServiceImpl implements ArticlesService {

    private final HttpClient httpClient;
    private final ArticlesMapper mapper;

    @Value("${articles.api.url}")
    private String articlesApiUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticlesServiceImpl.class);
    private static final String RESPONSE_BODY_LOGGER = "Response body: {}";

    @Override
    public List<ArticlesDataResponseDTO> getArticles() {
        LOGGER.info("Getting articles from downstream API");
        ArticlesResponse articlesResponse;

        try {
            ResponseEntity<ArticlesResponse> response = httpClient.invokeService(
                    UriComponentsBuilder.fromUriString(articlesApiUrl).queryParam("page", "1").toUriString(),
                    getBasicHttpHeaders(), new ParameterizedTypeReference<>() {
                    }, HttpMethod.GET);
            articlesResponse = response.hasBody() ? response.getBody() : null;
        } catch (Exception e) {
            throw new IllegalStateException("Error getting articles from downstream API. Error message: " + e.getMessage(), e);
        }

        if (Objects.isNull(articlesResponse) || articlesResponse.getData().isEmpty()) {
            throw new StandardException(COMMON_ERROR_404_005);
        }

        LOGGER.info(RESPONSE_BODY_LOGGER, articlesResponse);
        return mapper.responseToResponseDTO(articlesResponse.getData());
    }
}