package com.erebelo.springmongodbdemo.rest;

import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class HttpClientAuth extends AbstractRestTemplate {

    @Autowired
    public HttpClientAuth(@Value("${connection.timeout:3000}") String connTimeout,
            @Value("${connection.read.timeout:5000}") String connReadTimeout)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(new ConnectionProps(connTimeout, new ConnectionProps.Read(connReadTimeout)));
    }

    protected void restTemplateSetup() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try {
            log.info("Instantiating HttpClientAuth RestTemplate");
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(buildHttpClient());
            requestFactory.setConnectTimeout(Integer.parseInt(this.connProps.getTimeout()));
            requestFactory.setConnectionRequestTimeout(Integer.parseInt(this.connProps.getTimeout()));

            this.restTemplate.setRequestFactory(requestFactory);
        } catch (Exception e) {
            log.error("Error creating HttpClientAuth RestTemplate");
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
