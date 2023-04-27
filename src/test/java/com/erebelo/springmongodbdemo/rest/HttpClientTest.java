//package com.erebelo.springmongodbdemo.rest;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Collections;
//
//import static org.assertj.core.api.BDDAssertions.then;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class HttpClientTest {
//
//    @InjectMocks
//    private HttpClient httpClient;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private static final String RESPONSE_ENTITY = "Foo";
//
//    @Test
//    void testProperties() {
//        ConnectionProps props = new ConnectionProps();
//        ConnectionProps.Read read = new ConnectionProps.Read();
//        props.setTimeout("5000");
//        read.setTimeout("3000");
//        props.setRead(read);
//
//        assertEquals("5000", props.getTimeout());
//        assertEquals("3000", props.getRead().getTimeout());
//    }
//
//    @Test
//    void testHttpClientInstantiationWithoutParameters() throws Exception {
//        httpClient = new HttpClient();
//        assertNotNull(httpClient);
//    }
//
//    @Test
//    void testHttpClientInstantiationWithParameters() throws Exception {
//        httpClient = new HttpClient(new RestTemplate(), new ConnectionProps());
//        assertNotNull(httpClient);
//    }
//
//    @Test
//    void testHttpClientInstantiationWithParametersThenThrowException() {
//        try {
//            new HttpClient(null, new ConnectionProps());
//        } catch (Exception e) {
//            then(e.getClass()).isEqualTo(NullPointerException.class);
//        }
//    }
//
//    @Test
//    void testInvokeService() {
//        ResponseEntity<String> mockResponse = ResponseEntity.ok(RESPONSE_ENTITY);
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
//                .thenReturn(mockResponse);
//
//        ResponseEntity response = httpClient.invokeService("URL", createHeaders(), mock(ParameterizedTypeReference.class), HttpMethod.GET);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(RESPONSE_ENTITY, response.getBody());
//    }
//
//    @Test
//    void testInvokeServiceWithRequestObject() {
//        ResponseEntity<String> mockResponse = ResponseEntity.ok(RESPONSE_ENTITY);
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
//
//        ResponseEntity response = httpClient.invokeService("URL", createHeaders(), "myObj", mock(ParameterizedTypeReference.class), HttpMethod.GET);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(RESPONSE_ENTITY, response.getBody());
//    }
//
//    @Test
//    void testInvokeServiceWithUriVariables() {
//        ResponseEntity<String> mockResponse = ResponseEntity.ok(RESPONSE_ENTITY);
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class),
//                any(Object[].class))).thenReturn(mockResponse);
//
//        ResponseEntity response = httpClient.invokeService("URL", createHeaders(), "myObj", mock(ParameterizedTypeReference.class), HttpMethod.GET,
//                new Object[0]);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(RESPONSE_ENTITY, response.getBody());
//    }
//
//    @Test
//    void testInvokeServiceWithResponseTypeAndUriVariables() {
//        ResponseEntity<Object> mockResponse = ResponseEntity.ok(RESPONSE_ENTITY);
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), any(Object[].class))).thenReturn(mockResponse);
//
//        ResponseEntity response = httpClient.invokeService("URL", createHeaders(), "myObj", Object.class, HttpMethod.GET, new Object[0]);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(RESPONSE_ENTITY, response.getBody());
//    }
//
//    private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("Auth", Collections.singletonList("test"));
//        return headers;
//    }
//}
