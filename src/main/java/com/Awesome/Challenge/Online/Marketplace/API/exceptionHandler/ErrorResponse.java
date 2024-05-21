package com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private final int code;
    private final String message;
    private final String stackTrace;

    public ErrorResponse(ErrorCode errorCode, String stackTrace) {
        this.code = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
        this.stackTrace = stackTrace;
    }
}
