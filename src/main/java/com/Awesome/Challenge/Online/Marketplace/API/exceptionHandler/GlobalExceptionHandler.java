package com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
