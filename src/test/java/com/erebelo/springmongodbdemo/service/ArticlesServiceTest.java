package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.rest.HttpClient;
import com.erebelo.springmongodbdemo.service.impl.ArticlesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_003;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.ARTICLES_URL;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.TOTAL_PAGES;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesDataResponseDTO;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesDataResponseDTONextPage;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesResponse;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesResponseNextPage;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getBasicHttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticlesServiceImplTest {

    @InjectMocks
    private ArticlesServiceImpl service;

    @Mock
    private HttpClient httpClient;

    @Spy
    private final ArticlesMapper mapper = Mappers.getMapper(ArticlesMapper.class);

    @Captor
    private ArgumentCaptor<HttpHeaders> httpHeadersArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "articlesApiUrl", ARTICLES_URL);
    }

    @Test
    void testGetArticlesSuccessfully() {
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=1"), any(), any(), any())).willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=2"), any(), any(), any())).willReturn(ResponseEntity.ok(getArticlesResponseNextPage()));

        var result = service.getArticles();

        var articlesResponseDTO = new ArrayList<ArticlesDataResponseDTO>();
        articlesResponseDTO.add(getArticlesDataResponseDTO().get(0));
        articlesResponseDTO.add(getArticlesDataResponseDTONextPage().get(0));

        assertThat(result).hasSize(TOTAL_PAGES);
        assertThat(result).usingRecursiveComparison().isEqualTo(articlesResponseDTO);

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=1"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=2"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowNotFoundException() {
        given(httpClient.invokeService(anyString(), any(), any(), any())).willReturn(ResponseEntity.ok(null));

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.getArticles())
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_422_003);

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=1"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        verify(mapper, never()).responseToResponseDTO(anyList());

        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());
    }

    @Test
    void testGetArticlesIgnoreExceptionWhenHittingArticlesSecondTime() {
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=1"), any(), any(), any())).willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=2"), any(), any(), any())).willThrow(new RuntimeException("Async error"));

        var result = service.getArticles();

        assertThat(result).hasSize(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(getArticlesDataResponseDTO());

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=1"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=2"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesIgnoreCompletionException() {
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=1"), any(), any(), any())).willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(httpClient.invokeService(eq(ARTICLES_URL + "?page=2"), any(), any(), any())).willAnswer(invocation -> {
            CompletableFuture<ArticlesResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new CompletionException(new RuntimeException("Async error")));
            return future;
        });
        var result = service.getArticles();

        assertThat(result).hasSize(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(getArticlesDataResponseDTO());

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=1"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(httpClient).invokeService(eq(ARTICLES_URL + "?page=2"), httpHeadersArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class), eq(HttpMethod.GET));
        assertThat(httpHeadersArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }
}
