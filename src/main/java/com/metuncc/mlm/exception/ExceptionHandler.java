package com.metuncc.mlm.exception;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler   extends ResponseEntityExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MLMException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(MLMException e) {
        ExceptionResponse response = new ExceptionResponse(e.getExceptionCode().name(), e.getExceptionCode().getDescription());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
