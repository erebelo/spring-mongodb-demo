package com.erebelo.springmongodbdemo.exception.model;

public enum CommonErrorCodesEnum implements ErrorCode {

    COMMON_ERROR_400_000, COMMON_ERROR_400_001, COMMON_ERROR_401_000, COMMON_ERROR_404_001, COMMON_ERROR_404_002, COMMON_ERROR_404_003, COMMON_ERROR_404_004, COMMON_ERROR_404_005, COMMON_ERROR_409_001, COMMON_ERROR_422_000, COMMON_ERROR_422_001, COMMON_ERROR_422_002, COMMON_ERROR_500_000;

    private final String propertyKey;

    CommonErrorCodesEnum() {
        this.propertyKey = this.name().replace('_', '-');
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }
}
