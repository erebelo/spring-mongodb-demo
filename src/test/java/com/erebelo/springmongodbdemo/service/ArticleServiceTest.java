package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_001;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.ARTICLES_URL;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.TOTAL_PAGES;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.getArticleDataResponseDTO;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.getArticleDataResponseDTONextPage;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.getArticleResponse;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.getArticleResponseNextPage;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getDownstreamApiHttpHeaders;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticleResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ArticleMapper;
import com.erebelo.springmongodbdemo.service.impl.ArticleServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleServiceImpl service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Executor asyncTaskExecutor;

    @Spy
    private final ArticleMapper mapper = Mappers.getMapper(ArticleMapper.class);

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityArgumentCaptor;

    private MockedStatic<RequestContextHolder> mockedStatic;

    @BeforeEach
    void init() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("RequestID", "12345");

        mockedStatic = mockStatic(RequestContextHolder.class);
        mockedStatic.when(RequestContextHolder::getRequestAttributes)
                .thenReturn(new ServletRequestAttributes(mockHttpServletRequest));

        ReflectionTestUtils.setField(service, "articleApiUrl", ARTICLES_URL);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void testGetArticlesSuccessful() {
        willAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).given(asyncTaskExecutor).execute(any(Runnable.class));

        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any()))
                .willReturn(ResponseEntity.ok(getArticleResponse()));
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=2"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any()))
                .willReturn(ResponseEntity.ok(getArticleResponseNextPage()));

        List<ArticleDataResponseDTO> response = service.getArticles();

        List<ArticleDataResponseDTO> articlesResponseDTO = new ArrayList<>();
        articlesResponseDTO.add(getArticleDataResponseDTO().get(0));
        articlesResponseDTO.add(getArticleDataResponseDTONextPage().get(0));

        assertThat(response).hasSize(TOTAL_PAGES);
        assertThat(response).usingRecursiveComparison().isEqualTo(articlesResponseDTO);

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowsNotFoundException() {
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<?>>any())).willReturn(ResponseEntity.notFound().build());

        CommonException exception = assertThrows(CommonException.class, () -> service.getArticles());

        assertEquals(COMMON_ERROR_404_005, exception.getErrorCode());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), ArgumentMatchers.<ParameterizedTypeReference<?>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(mapper, never()).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowsRestClientExceptionWhenHittingArticlesSecondTime() {
        willAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).given(asyncTaskExecutor).execute(any(Runnable.class));

        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any()))
                .willReturn(ResponseEntity.ok(getArticleResponse()));
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=2"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .willThrow(new RestClientException("Async error"));

        ClientException exception = assertThrows(ClientException.class, () -> service.getArticles());

        assertInstanceOf(RestClientException.class, exception.getCause());
        assertEquals("Error getting articles from downstream API for page: 2. Error message: Async error",
                exception.getMessage());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), ArgumentMatchers.<ParameterizedTypeReference<?>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(mapper, never()).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowsCommonExceptionWhenHittingArticlesSecondTime() {
        willAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).given(asyncTaskExecutor).execute(any(Runnable.class));

        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any()))
                .willReturn(ResponseEntity.ok(getArticleResponse()));
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=2"), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .willThrow(new RuntimeException("Async processing failed"));

        CommonException exception = assertThrows(CommonException.class, () -> service.getArticles());

        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals(COMMON_ERROR_500_001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Async processing failed"));

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<ArticleResponse>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), ArgumentMatchers.<ParameterizedTypeReference<?>>any());
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());

        verify(mapper, never()).responseToResponseDTO(anyList());
    }
}
