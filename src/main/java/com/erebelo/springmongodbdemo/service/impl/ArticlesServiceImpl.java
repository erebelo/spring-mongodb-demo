package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.rest.HttpClientAuth;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_003;
import static com.erebelo.springmongodbdemo.util.AuthenticationUtil.getBasicHttpHeaders;

@Service
@RequiredArgsConstructor
public class ArticlesServiceImpl implements ArticlesService {

    private final HttpClientAuth httpClientAuth;
    private final ArticlesMapper mapper;

    @Value("${articles.api.url}")
    private String articlesApiUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticlesServiceImpl.class);
    private static final String MDC_KEY_REQUEST_ID = "requestId";
    private static final int INITIAL_PAGE = 1;


    @Override
    public List<ArticlesDataResponseDTO> getArticles() {
        LOGGER.info("Getting articles from downstream API: {}", articlesApiUrl);
        Integer totalPages = INITIAL_PAGE;
        var mdcKey = MDC.get(MDC_KEY_REQUEST_ID);

        ArticlesResponse firstArticlesResponse = fetchData(INITIAL_PAGE, mdcKey, false);

        if (Objects.nonNull(firstArticlesResponse)) {
            totalPages = firstArticlesResponse.getTotalPages();
        } else {
            LOGGER.warn("Empty or null response from first articles downstream API call");
        }

        List<ArticlesResponse> allArticlesResponses = new ArrayList<>();
        if (Objects.nonNull(totalPages) && totalPages > INITIAL_PAGE) {
            long startTime = System.currentTimeMillis();

            LOGGER.info("Fetching articles asynchronously through CompletableFuture");
            List<CompletableFuture<ArticlesResponse>> futures = IntStream.rangeClosed(INITIAL_PAGE + 1, totalPages)
                    .mapToObj(page -> CompletableFuture.supplyAsync(() -> fetchData(page, mdcKey, true)))
                    .toList();

            LOGGER.info("Combining all CompletableFuture results");
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            CompletableFuture<Void> exceptionalResult = allOf.exceptionally(throwable -> {
                LOGGER.error("Error waiting for CompletableFuture to complete. Error message: {}", throwable.getCause().getMessage());
                return null;
            });

            LOGGER.info("Waiting for all CompletableFuture to complete");
            exceptionalResult.join();

            long totalTime = System.currentTimeMillis() - startTime;
            LOGGER.info("Total time taken to retrieve {} article pages asynchronously: {} milliseconds", totalPages, totalTime);

            LOGGER.info("Collecting results from completed CompletableFutures");
            allArticlesResponses = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (Objects.nonNull(firstArticlesResponse)) {
            allArticlesResponses.add(0, firstArticlesResponse);
        }

        List<ArticlesDataResponse> allArticlesDataResponses = allArticlesResponses.stream()
                .filter(response -> response.getData() != null)
                .flatMap(response -> response.getData().stream())
                .toList();

        if (allArticlesDataResponses.isEmpty()) {
            throw new StandardException(COMMON_ERROR_422_003);
        }

        LOGGER.info("{} articles found", allArticlesDataResponses.size());
        return mapper.responseToResponseDTO(allArticlesDataResponses);
    }

    private ArticlesResponse fetchData(int page, String mdcKey, boolean isAsync) {
        try {
            MDC.put(MDC_KEY_REQUEST_ID, mdcKey); // Setting requestId for asynchronous logs
            LOGGER.info("Retrieving articles for page {}", page);

            ResponseEntity<ArticlesResponse> response = httpClientAuth.getRestTemplate().exchange(
                    UriComponentsBuilder.fromUriString(articlesApiUrl).queryParam("page", page).toUriString(), HttpMethod.GET,
                    new HttpEntity<>(getBasicHttpHeaders()), new ParameterizedTypeReference<>() {
                    });

            LOGGER.info("Articles for page {} retrieved successfully", page);
            return response.hasBody() ? response.getBody() : null;
        } catch (Exception e) {
            LOGGER.error("Error getting articles from downstream API for page: {}. Error message: {}", page, e.getMessage());
            return null;
        } finally {
            if (isAsync) {
                MDC.remove(MDC_KEY_REQUEST_ID);
            }
        }
    }
}
