package com.erebelo.springmongodbdemo.rest;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

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
            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            clientBuilder.useSystemProperties();

            LOGGER.info("Setting SSL certificate");
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true);
            clientBuilder.setSSLContext(sslContextBuilder.build()).setSSLHostnameVerifier(new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault()));
            CloseableHttpClient client = clientBuilder.disableCookieManagement().build();

            var factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(client);
            factory.setConnectTimeout(Integer.parseInt(this.connProps.getTimeout()));
            factory.setReadTimeout(Integer.parseInt(this.connProps.getRead().getTimeout()));

            this.restTemplate.setRequestFactory(factory);
        } catch (Exception e) {
            LOGGER.error("Error creating HttpClientAuth RestTemplate");
            throw e;
        }
    }
}
