package com.erebelo.springmongodbdemo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.DEFAULT_CONNECTION_TIMEOUT;
import static com.erebelo.springmongodbdemo.constants.BusinessConstants.DEFAULT_SOCKET_TIMEOUT;

abstract class AbstractRestTemplate {

    protected RestTemplate restTemplate;
    protected ConnectionProps connProps;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final String INVOKE_SERVICE_MESSAGE = "Invoking the service by URL: {}";

    protected AbstractRestTemplate(ConnectionProps connProps) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.restTemplate = new RestTemplate();
        this.connProps = validateConnectionProps(connProps);
        restTemplateSetup();
    }

    protected abstract void restTemplateSetup() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

    private ConnectionProps validateConnectionProps(ConnectionProps connProps) {
        if (Objects.isNull(connProps)) {
            connProps = new ConnectionProps();
        }

        if (Objects.isNull(connProps.getTimeout()) || ObjectUtils.isEmpty(connProps.getTimeout())) {
            connProps.setTimeout(DEFAULT_CONNECTION_TIMEOUT);
        }

        if (Objects.isNull(connProps.getRead()) || Objects.isNull(connProps.getRead().getTimeout()) || ObjectUtils.isEmpty(connProps.getRead().getTimeout())) {
            connProps.setRead(new ConnectionProps.Read(DEFAULT_SOCKET_TIMEOUT));
        }

        return connProps;
    }

    public <T> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final ParameterizedTypeReference<T> responseType,
            HttpMethod httpMethod) throws RestClientException {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        LOGGER.info(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final ParameterizedTypeReference<T> responseType, HttpMethod httpMethod) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.info(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final ParameterizedTypeReference<T> responseType, HttpMethod httpMethod, final Object... uriVariables) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.info(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final Class<T> responseType, HttpMethod httpMethod, final Object... uriVariables) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.info(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables);
    }
}
