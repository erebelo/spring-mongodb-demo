package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getHttpHeaders;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getServletRequestAttributes;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.WIKIMEDIA_URL;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.getWikimediaResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.rest.HttpClient;
import com.erebelo.springmongodbdemo.service.impl.WikimediaServiceImpl;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

@ExtendWith(MockitoExtension.class)
class WikimediaServiceTest {

    @InjectMocks
    private WikimediaServiceImpl service;

    @Mock
    private HttpClient httpClient;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityArgumentCaptor;

    private final MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "wikimediaPublicApiUrl", WIKIMEDIA_URL);
        given(httpClient.getRestTemplate()).willReturn(mock(RestTemplate.class));
        mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(getServletRequestAttributes());
    }

    @AfterEach
    void clear() {
        if (Objects.nonNull(mockedStatic)) {
            mockedStatic.close();
        }
    }

    @Test
    void testGetWikimediaProjectPageviewsSuccessfully() {
        given(httpClient.getRestTemplate().exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(getWikimediaResponse()));

        var result = service.getWikimediaProjectPageviews();

        assertThat(result).usingRecursiveComparison().isEqualTo(getWikimediaResponse());

        verify(httpClient.getRestTemplate()).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getHttpHeaders());
    }

    @Test
    void testGetWikimediaProjectPageviewsThrowsRestClientException() {
        given(httpClient.getRestTemplate().exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .willThrow(new RestClientException("Internal Server Error"));

        assertThatExceptionOfType(ClientException.class).isThrownBy(() -> service.getWikimediaProjectPageviews())
                .withCauseExactlyInstanceOf(RestClientException.class)
                .withMessage("Error getting Wikimedia project pageviews: Internal Server Error");

        verify(httpClient.getRestTemplate()).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getHttpHeaders());
    }

    @Test
    void testGetWikimediaProjectPageviewsThrowsNotFoundException() {
        given(httpClient.getRestTemplate().exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(new WikimediaResponse()));

        assertThatExceptionOfType(CommonException.class).isThrownBy(() -> service.getWikimediaProjectPageviews())
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_004);

        verify(httpClient.getRestTemplate()).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET),
                httpEntityArgumentCaptor.capture(), any(ParameterizedTypeReference.class));

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getHttpHeaders());
    }
}
