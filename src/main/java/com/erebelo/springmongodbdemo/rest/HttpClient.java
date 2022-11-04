package com.erebelo.springmongodbdemo.rest;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.DEFAULT_READ_TIMEOUT;
import static com.erebelo.springmongodbdemo.constants.BusinessConstants.DEFAULT_SOCKET_TIMEOUT;

@Component
public class HttpClient {

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Value("S{connection.timeout:3000}")
    private String connTimeout;

    @Value("S{connection.read.timeout:5000}")
    private String connReadTimeout;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final String INVOKE_SERVICE_MESSAGE = "Invoking the service by URL: {}";

    public HttpClient() {
        this(new RestTemplate(), new ConnectionProps());
    }

    public HttpClient(RestTemplate restTemplate, ConnectionProps connProps) {
        LOGGER.info("Instantiating RestTemplate http client");
        this.restTemplate = restTemplate;

        if (Objects.isNull(connProps)) {
            connProps = new ConnectionProps();
        }
        if (Objects.isNull(connProps.getTimeout())) {
            connProps.setTimeout(Objects.nonNull(connTimeout) ? connTimeout : DEFAULT_SOCKET_TIMEOUT);
        }
        if (Objects.isNull(connProps.getRead()) || Objects.isNull(connProps.getRead().getTimeout())) {
            ConnectionProps.Read read = new ConnectionProps.Read();
            read.setTimeout(Objects.nonNull(connReadTimeout) ? connReadTimeout : DEFAULT_READ_TIMEOUT);
            connProps.setRead(read);
        }

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.useSystemProperties();
        try {
            String proxy = System.getenv("http_proxy");
            if (Objects.nonNull(proxy) && !proxy.isEmpty()) {
                LOGGER.info("Setting proxy for ServiceTemplate");
                URL urlProxy = new URL(proxy);
                LOGGER.info("Proxy: {}, Host: {}, Port: {}", urlProxy, urlProxy.getHost(), urlProxy.getPort());
                clientBuilder.setProxy(new HttpHost(urlProxy.getHost(), urlProxy.getPort()));

                if (urlProxy.getUserInfo() != null) {
                    String[] userInfo = urlProxy.getUserInfo().split(":");
                    LOGGER.info("User: {}", userInfo[0]);

                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(new AuthScope(urlProxy.getHost(), urlProxy.getPort()),
                            new UsernamePasswordCredentials(userInfo[0], userInfo[1]));

                    clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
                }
            }

            LOGGER.info("Setting SSL certificate");
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true);
            clientBuilder.setSSLContext(sslContextBuilder.build()).setSSLHostnameVerifier(new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault()));
            CloseableHttpClient client = clientBuilder.disableCookieManagement().build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(client);
            factory.setConnectTimeout(Integer.parseInt(connProps.getTimeout()));
            factory.setReadTimeout(Integer.parseInt(connProps.getRead().getTimeout()));
            this.restTemplate.setRequestFactory(factory);
        } catch (Exception e) {
            LOGGER.error("ReatTemplate error: RestTemplate creation with proxy and authentication has been not completed");
        }
    }

    public <T> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final ParameterizedTypeReference<T> responseType,
            HttpMethod httpMethod) throws RestClientException {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        LOGGER.debug(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final ParameterizedTypeReference<T> responseType, HttpMethod httpMethod) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.debug(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final ParameterizedTypeReference<T> responseType, HttpMethod httpMethod, final Object... uriVariables) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.debug(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables);
    }

    public <T, K> ResponseEntity<T> invokeService(final String url, final HttpHeaders headers, final K requestObject,
            final Class<T> responseType, HttpMethod httpMethod, final Object... uriVariables) throws RestClientException {
        HttpEntity<K> requestEntity = new HttpEntity<>(requestObject, headers);
        LOGGER.debug(INVOKE_SERVICE_MESSAGE, url);
        return restTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables);
    }
}
