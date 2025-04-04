package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.mock.HttpHeadersMock.getDownstreamApiHttpHeaders;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.WIKIMEDIA_URL;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.getWikimediaResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.impl.WikimediaServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
class WikimediaServiceTest {

    @InjectMocks
    private WikimediaServiceImpl service;

    @Mock
    private RestTemplate restTemplate;

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

        ReflectionTestUtils.setField(service, "wikimediaPublicApiUrl", WIKIMEDIA_URL);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void testGetWikimediaProjectPageviewsSuccessful() {
        given(restTemplate.exchange(anyString(), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<WikimediaResponse>>any()))
                .willReturn(ResponseEntity.ok(getWikimediaResponse()));

        WikimediaResponse response = service.getWikimediaProjectPageviews();

        assertThat(response).usingRecursiveComparison().isEqualTo(getWikimediaResponse());

        verify(restTemplate).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<WikimediaResponse>>any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());
    }

    @Test
    void testGetWikimediaProjectPageviewsThrowsRestClientException() {
        given(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .willThrow(new RestClientException("Internal Server Error"));

        ClientException exception = assertThrows(ClientException.class, () -> service.getWikimediaProjectPageviews());

        assertInstanceOf(RestClientException.class, exception.getCause());
        assertEquals("Error getting Wikimedia project pageviews. Error message: Internal Server Error",
                exception.getMessage());

        verify(restTemplate).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<?>>any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());
    }

    @Test
    void testGetWikimediaProjectPageviewsThrowsNotFoundException() {
        given(restTemplate.exchange(anyString(), any(), any(),
                ArgumentMatchers.<ParameterizedTypeReference<WikimediaResponse>>any()))
                .willReturn(ResponseEntity.ok(new WikimediaResponse()));

        CommonException exception = assertThrows(CommonException.class, () -> service.getWikimediaProjectPageviews());

        assertEquals(COMMON_ERROR_404_004, exception.getErrorCode());

        verify(restTemplate).exchange(eq(WIKIMEDIA_URL), eq(HttpMethod.GET), httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<ParameterizedTypeReference<WikimediaResponse>>any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).usingRecursiveComparison()
                .isEqualTo(getDownstreamApiHttpHeaders());
    }
}
