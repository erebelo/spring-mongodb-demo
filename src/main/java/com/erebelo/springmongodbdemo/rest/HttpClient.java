package com.erebelo.springmongodbdemo.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Log4j2
@Component
public class HttpClient extends AbstractRestTemplate {

    @Autowired
    public HttpClient(@Value("${connection.timeout:3000}") String connTimeout,
            @Value("${connection.read.timeout:5000}") String connReadTimeout)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(new ConnectionProps(connTimeout, new ConnectionProps.Read(connReadTimeout)));
    }

    protected void restTemplateSetup() {
        try {
            log.info("Instantiating HttpClient RestTemplate");
            var factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Integer.parseInt(this.connProps.getTimeout()));
            factory.setReadTimeout(Integer.parseInt(this.connProps.getRead().getTimeout()));

            this.restTemplate.setRequestFactory(factory);
        } catch (Exception e) {
            log.error("Error creating HttpClient RestTemplate");
            throw e;
        }
    }
}
