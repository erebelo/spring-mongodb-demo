package com.erebelo.springmongodbdemo.rest;

import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.DEFAULT_CONNECTION_TIMEOUT;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.DEFAULT_SOCKET_TIMEOUT;

abstract class AbstractRestTemplate {

    protected RestTemplate restTemplate;
    protected ConnectionProps connProps;

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

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
