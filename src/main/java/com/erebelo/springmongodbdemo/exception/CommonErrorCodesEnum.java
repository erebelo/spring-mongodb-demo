package com.erebelo.springmongodbdemo.exception;

public enum CommonErrorCodesEnum implements ErrorCode {

    COMMON_ERROR_400_000(),
    COMMON_ERROR_401_000(),
    COMMON_ERROR_422_000(),
    COMMON_ERROR_500_000(),
    COMMON_ERROR_404_001(),
    COMMON_ERROR_404_002(),
    COMMON_ERROR_404_003(),
    COMMON_ERROR_409_001();

    private final String propertyKey;

    CommonErrorCodesEnum() {
        this.propertyKey = this.name().replace('_', '-');
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }
}
