package com.erebelo.springmongodbdemo.rest;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Component
public class HttpClientAuth extends AbstractRestTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientAuth.class);

    @Autowired
    public HttpClientAuth(@Value("${connection.timeout:3000}") String connTimeout, @Value("${connection.read.timeout:5000}") String connReadTimeout)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(new ConnectionProps(connTimeout, new ConnectionProps.Read(connReadTimeout)));
    }

    protected void restTemplateSetup() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try {
            LOGGER.info("Instantiating HttpClientAuth RestTemplate");
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(buildHttpClient());
            requestFactory.setConnectTimeout(Integer.parseInt(this.connProps.getTimeout()));
            requestFactory.setConnectionRequestTimeout(Integer.parseInt(this.connProps.getTimeout()));

            this.restTemplate.setRequestFactory(requestFactory);
        } catch (Exception e) {
            LOGGER.error("Error creating HttpClientAuth RestTemplate");
            throw e;
        }
    }

    private CloseableHttpClient buildHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig())
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(SSLContextBuilder.create()
                                        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                        .build())
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .build();
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setResponseTimeout(Long.parseLong(this.connProps.getRead().getTimeout()), TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(Long.parseLong(this.connProps.getTimeout()), TimeUnit.MILLISECONDS)
                .build();
    }
}
