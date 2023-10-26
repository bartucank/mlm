package com.metuncc.mlm.exception;

public class MLMException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public MLMException(ExceptionCode exceptionCode) {
        super(exceptionCode.getDescription());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
