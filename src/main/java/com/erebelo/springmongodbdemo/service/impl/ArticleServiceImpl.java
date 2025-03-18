package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_001;
import static com.erebelo.springmongodbdemo.util.HttpHeadersUtil.getDownstreamApiHttpHeaders;

import com.erebelo.spring.common.utils.threading.AsyncThreadContext;
import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticleResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ArticleMapper;
import com.erebelo.springmongodbdemo.service.ArticleService;
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
public class ArticleServiceImpl implements ArticleService {

    private final RestTemplate restTemplate;
    private final Executor asyncTaskExecutor;
    private final ArticleMapper mapper;

    @Value("${article.api.url}")
    private String articleApiUrl;

    private static final int INITIAL_PAGE = 1;
    private static final String ARTICLE_CLIENT_ERROR_MESSAGE = "Error getting articles from downstream API for page:"
            + " %d. Error message: %s";

    @Override
    public List<ArticleDataResponseDTO> getArticles() {
        log.info("Fetching articles from downstream API: {}", articleApiUrl);
        Integer totalPages = INITIAL_PAGE;

        ArticleResponse firstArticleResponse = fetchData(INITIAL_PAGE);

        if (Objects.nonNull(firstArticleResponse)) {
            totalPages = firstArticleResponse.getTotalPages();
        } else {
            log.warn("Empty or null response from first article downstream API call");
        }

        List<ArticleResponse> allArticleResponses = new ArrayList<>();
        if (Objects.nonNull(totalPages) && totalPages > INITIAL_PAGE) {
            long startTime = System.currentTimeMillis();

            /*
             * Creates async tasks to fetch data concurrently, using AsyncThreadContext to
             * preserve the logging and request context across threads. The thread pool is
             * provided by asyncTaskExecutor, ensuring consistent logging in downstream
             * calls.
             */
            log.info("Fetching articles asynchronously through CompletableFuture");
            List<CompletableFuture<ArticleResponse>> futures = IntStream.rangeClosed(INITIAL_PAGE + 1, totalPages)
                    .mapToObj(page -> CompletableFuture.supplyAsync(
                            AsyncThreadContext.withThreadContext(() -> fetchData(page)), asyncTaskExecutor))
                    .toList();

            try {
                log.info("Waiting for all CompletableFuture to complete");
                allArticleResponses = futures.stream().map(CompletableFuture::join).filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (CompletionException e) {
                if (e.getCause() instanceof ClientException clientException) {
                    throw clientException;
                }
                throw new CommonException(COMMON_ERROR_500_001, e);
            }

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Total time taken to retrieve {} article pages asynchronously: {} milliseconds", totalPages,
                    totalTime);
        }

        if (Objects.nonNull(firstArticleResponse)) {
            allArticleResponses.add(0, firstArticleResponse);
        }

        List<ArticleDataResponse> allArticleDataResponses = allArticleResponses.stream()
                .filter(response -> response.getData() != null).flatMap(response -> response.getData().stream())
                .toList();

        if (allArticleDataResponses.isEmpty()) {
            throw new CommonException(COMMON_ERROR_404_005);
        }

        log.info("{} articles found", allArticleDataResponses.size());
        return mapper.responseToResponseDTO(allArticleDataResponses);
    }

    private ArticleResponse fetchData(int page) {
        try {
            log.info("Retrieving articles for page {}", page);
            ResponseEntity<ArticleResponse> response = restTemplate.exchange(
                    UriComponentsBuilder.fromUriString(articleApiUrl).queryParam("page", page).toUriString(),
                    HttpMethod.GET, new HttpEntity<>(getDownstreamApiHttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    });

            log.info("Articles for page {} retrieved successfully", page);
            return response.hasBody() ? response.getBody() : null;
        } catch (RestClientException e) {
            throw new ClientException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format(ARTICLE_CLIENT_ERROR_MESSAGE, page, e.getMessage()), e);
        }
    }
}
