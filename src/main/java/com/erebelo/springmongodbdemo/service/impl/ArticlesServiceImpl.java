package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_003;
import static com.erebelo.springmongodbdemo.util.HttpHeadersUtil.getArticlesHttpHeaders;

import com.erebelo.spring.common.utils.threading.AsyncThreadContext;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

            log.info("Combining all CompletableFuture results");
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            CompletableFuture<Void> exceptionalResult = allOf.exceptionally(throwable -> {
                log.warn("Error waiting for CompletableFuture to complete. Error message: {}",
                        throwable.getCause().getMessage());
                return null;
            });

            log.info("Waiting for all CompletableFuture to complete");
            exceptionalResult.join();

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Total time taken to retrieve {} article pages asynchronously: {} milliseconds", totalPages,
                    totalTime);

            log.info("Collecting results from completed CompletableFutures");
            allArticlesResponses = futures.stream().map(CompletableFuture::join).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (Objects.nonNull(firstArticlesResponse)) {
            allArticlesResponses.add(0, firstArticlesResponse);
        }

        List<ArticlesDataResponse> allArticlesDataResponses = allArticlesResponses.stream()
                .filter(response -> response.getData() != null).flatMap(response -> response.getData().stream())
                .toList();

        if (allArticlesDataResponses.isEmpty()) {
            throw new CommonException(COMMON_ERROR_422_003);
        }

        log.info("{} articles found", allArticlesDataResponses.size());
        return mapper.responseToResponseDTO(allArticlesDataResponses);
    }

    private ArticlesResponse fetchData(int page) {
        try {
            log.info("Retrieving articles for page {}", page);
            ResponseEntity<ArticlesResponse> response = restTemplate.exchange(
                    UriComponentsBuilder.fromUriString(articlesApiUrl).queryParam("page", page).toUriString(),
                    HttpMethod.GET, new HttpEntity<>(getArticlesHttpHeaders()), new ParameterizedTypeReference<>() {
                    });

            log.info("Articles for page {} retrieved successfully", page);
            return response.hasBody() ? response.getBody() : null;
        } catch (RestClientException e) {
            log.warn("Error getting articles from downstream API for page: {}. Error message: {}", page,
                    e.getMessage());
            return null;
        }
    }
}
