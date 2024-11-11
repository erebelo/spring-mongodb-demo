package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.util.HttpHeadersUtil.getDownstreamApiHttpHeaders;

import com.erebelo.spring.common.utils.threading.AsyncThreadContext;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Service
@RequiredArgsConstructor
public class ArticlesServiceImpl implements ArticlesService {

    private final RestTemplate restTemplate;
    private final Executor asyncTaskExecutor;
    private final ArticlesMapper mapper;

    @Value("${articles.api.url}")
    private String articlesApiUrl;

    private static final int INITIAL_PAGE = 1;
    private static final String ARTICLES_CLIENT_ERROR_MESSAGE = "Error getting articles from downstream API for page:"
            + " %d. Error message: %s";

    @Override
    public List<ArticlesDataResponseDTO> getArticles() {
        log.info("Getting articles from downstream API: {}", articlesApiUrl);
        Integer totalPages = INITIAL_PAGE;

        ArticlesResponse firstArticlesResponse = fetchData(INITIAL_PAGE);

        if (Objects.nonNull(firstArticlesResponse)) {
            totalPages = firstArticlesResponse.getTotalPages();
        } else {
            log.warn("Empty or null response from first articles downstream API call");
        }

        List<ArticlesResponse> allArticlesResponses = new ArrayList<>();
        if (Objects.nonNull(totalPages) && totalPages > INITIAL_PAGE) {
            long startTime = System.currentTimeMillis();

            /*
             * Creates async tasks to fetch data concurrently, using
             * AsyncThreadContext.withThreadContext() to retain logging and request context
             * (e.g., headers, trace IDs) across threads. asyncTaskExecutor supplies the
             * thread pool, preserving main context for consistent logging in downstream API
             * calls.
             */
            log.info("Fetching articles asynchronously through CompletableFuture");
            List<CompletableFuture<ArticlesResponse>> futures = IntStream.rangeClosed(INITIAL_PAGE + 1, totalPages)
                    .mapToObj(page -> CompletableFuture.supplyAsync(
                            AsyncThreadContext.withThreadContext(() -> fetchData(page)), asyncTaskExecutor))
                    .toList();

            try {
                log.info("Waiting for all CompletableFuture to complete");
                allArticlesResponses = futures.stream().map(CompletableFuture::join).filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (CompletionException e) {
                if (e.getCause() instanceof ClientException) {
                    throw (ClientException) e.getCause();
                }
                throw new RuntimeException(e);
            }

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Total time taken to retrieve {} article pages asynchronously: {} milliseconds", totalPages,
                    totalTime);
        }

        if (Objects.nonNull(firstArticlesResponse)) {
            allArticlesResponses.add(0, firstArticlesResponse);
        }

        List<ArticlesDataResponse> allArticlesDataResponses = allArticlesResponses.stream()
                .filter(response -> response.getData() != null).flatMap(response -> response.getData().stream())
                .toList();

        if (allArticlesDataResponses.isEmpty()) {
            throw new CommonException(COMMON_ERROR_404_005);
        }

        log.info("{} articles found", allArticlesDataResponses.size());
        return mapper.responseToResponseDTO(allArticlesDataResponses);
    }

    private ArticlesResponse fetchData(int page) {
        try {
            log.info("Retrieving articles for page {}", page);
            ResponseEntity<ArticlesResponse> response = restTemplate.exchange(
                    UriComponentsBuilder.fromUriString(articlesApiUrl).queryParam("page", page).toUriString(),
                    HttpMethod.GET, new HttpEntity<>(getDownstreamApiHttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    });

            log.info("Articles for page {} retrieved successfully", page);
            return response.hasBody() ? response.getBody() : null;
        } catch (RestClientException e) {
            throw new ClientException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format(ARTICLES_CLIENT_ERROR_MESSAGE, page, e.getMessage()), e);
        }
    }
}
