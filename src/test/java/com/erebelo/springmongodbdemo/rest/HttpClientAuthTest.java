package com.erebelo.springmongodbdemo.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class HttpClientAuthTest {

    @InjectMocks
    private HttpClientAuth httpClientAuth;

    @Mock
    private ConnectionProps connectionProps;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(httpClientAuth, "connProps", connectionProps);
        ReflectionTestUtils.setField(httpClientAuth, "restTemplate", restTemplate);
    }

    @Test
    void testRestTemplateSetupSuccessfully() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        given(connectionProps.getTimeout()).willReturn("3000");
        given(connectionProps.getRead()).willReturn(new ConnectionProps.Read("5000"));

        httpClientAuth.restTemplateSetup();

        assertNotNull(httpClientAuth);
    }

    @Test
    void testRestTemplateSetupThrowsException() {
        given(connectionProps.getTimeout()).willReturn("3000");
        given(connectionProps.getRead()).willReturn(new ConnectionProps.Read("5000"));

        willThrow(new RuntimeException("Simulated restTemplate error")).given(restTemplate).setRequestFactory(any(ClientHttpRequestFactory.class));

        assertThrows(RuntimeException.class, () -> httpClientAuth.restTemplateSetup());
    }
}
