package com.erebelo.springmongodbdemo.exception;

import lombok.Getter;

@Getter
public class StandardException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public StandardException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    public StandardException(ErrorCode errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public StandardException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public StandardException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}
