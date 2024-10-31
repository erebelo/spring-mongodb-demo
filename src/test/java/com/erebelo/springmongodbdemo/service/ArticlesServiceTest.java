package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.ARTICLES_URL;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.TOTAL_PAGES;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesDataResponseDTO;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesDataResponseDTONextPage;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesResponse;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesResponseNextPage;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getArticlesHttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ArticlesMapper;
import com.erebelo.springmongodbdemo.service.impl.ArticlesServiceImpl;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
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
class ArticlesServiceTest {

    @InjectMocks
    private ArticlesServiceImpl service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Executor asyncTaskExecutor;

    @Spy
    private final ArticlesMapper mapper = Mappers.getMapper(ArticlesMapper.class);

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

        ReflectionTestUtils.setField(service, "articlesApiUrl", ARTICLES_URL);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void testGetArticlesSuccessfully() {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(asyncTaskExecutor).execute(any(Runnable.class));

        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=2"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponseNextPage()));

        var result = service.getArticles();

        var articlesResponseDTO = new ArrayList<ArticlesDataResponseDTO>();
        articlesResponseDTO.add(getArticlesDataResponseDTO().get(0));
        articlesResponseDTO.add(getArticlesDataResponseDTONextPage().get(0));

        assertThat(result).hasSize(TOTAL_PAGES);
        assertThat(result).usingRecursiveComparison().isEqualTo(articlesResponseDTO);

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getArticlesHttpHeaders());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getArticlesHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }

    @Test
    void testGetArticlesThrowsNotFoundException() {
        given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .willThrow(new RestClientException("Internal Server Error"));

        assertThatExceptionOfType(CommonException.class).isThrownBy(() -> service.getArticles())
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_005);

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));
        verify(mapper, never()).responseToResponseDTO(anyList());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getArticlesHttpHeaders());
    }

    @Test
    void testGetArticlesIgnoreExceptionWhenHittingArticlesSecondTime() {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(asyncTaskExecutor).execute(any(Runnable.class));

        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=1"), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getArticlesResponse()));
        given(restTemplate.exchange(eq(ARTICLES_URL + "?page=2"), any(), any(), any(ParameterizedTypeReference.class)))
                .willThrow(new RestClientException("Async error"));

        var result = service.getArticles();

        assertThat(result).hasSize(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(getArticlesDataResponseDTO());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=1"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getArticlesHttpHeaders());

        verify(restTemplate).exchange(eq(ARTICLES_URL + "?page=2"), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getArticlesHttpHeaders());

        verify(mapper).responseToResponseDTO(anyList());
    }
}
