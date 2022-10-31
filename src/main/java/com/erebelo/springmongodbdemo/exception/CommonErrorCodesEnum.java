package com.erebelo.springmongodbdemo.exception;

public enum CommonErrorCodesEnum implements ErrorCode {

    COMMON_ERROR_404_001();

    private final String propertyKey;

    CommonErrorCodesEnum() {
        this.propertyKey = this.name().replace('_', '-');
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }
}
