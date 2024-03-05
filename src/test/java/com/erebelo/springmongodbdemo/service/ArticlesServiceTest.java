package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.rest.HttpClientAuth;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticlesServiceImplTest {

    @InjectMocks
    private ArticlesServiceImpl service;

    @Mock
    private HttpClientAuth httpClientAuth;

    @Spy
    private final ArticlesMapper mapper = Mappers.getMapper(ArticlesMapper.class);

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "articlesApiUrl", ARTICLES_URL);
        given(httpClientAuth.getRestTemplate()).willReturn(mock(RestTemplate.class));
    }

    @Test
    void testGetArticlesSuccessfully() {
        given(httpClientAuth.getRestTemplate().exchange(eq(ARTICLES_URL + "?page=1"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(httpClientAuth.getRestTemplate().exchange(eq(ARTICLES_URL + "?page=2"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponseNextPage()));

        var result = service.getArticles();

        var articlesResponseDTO = new ArrayList<ArticlesDataResponseDTO>();
        articlesResponseDTO.add(getArticlesDataResponseDTO().get(0));
        articlesResponseDTO.add(getArticlesDataResponseDTONextPage().get(0));

        assertThat(result).hasSize(TOTAL_PAGES);
        assertThat(result).usingRecursiveComparison().isEqualTo(articlesResponseDTO);

        verify(httpClientAuth.getRestTemplate()).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(httpClientAuth.getRestTemplate()).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowNotFoundException() {
        given(httpClientAuth.getRestTemplate().exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class))).willReturn(null);

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.getArticles())
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_422_003);

        verify(httpClientAuth.getRestTemplate()).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class));
        verify(mapper, never()).responseToResponseDTO(anyList());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());
    }

    @Test
    void testGetArticlesIgnoreExceptionWhenHittingArticlesSecondTime() {
        given(httpClientAuth.getRestTemplate().exchange(eq(ARTICLES_URL + "?page=1"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(httpClientAuth.getRestTemplate().exchange(eq(ARTICLES_URL + "?page=2"), any(), any(), any(ParameterizedTypeReference.class)))
                .willThrow(new RuntimeException("Async error"));

        var result = service.getArticles();

        assertThat(result).hasSize(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(getArticlesDataResponseDTO());

        verify(httpClientAuth.getRestTemplate()).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(httpClientAuth.getRestTemplate()).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison().isEqualTo(getBasicHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }
}
