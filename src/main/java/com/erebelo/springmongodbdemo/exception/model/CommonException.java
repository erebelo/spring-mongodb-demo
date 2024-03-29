package com.erebelo.springmongodbdemo.exception.model;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    public CommonException(ErrorCode errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public CommonException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public CommonException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}
