package com.finalyear.VolunteeringSystm.exceptionHandler;

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

//    public ErrorResponse(int code, String message, String stackTrace) {
//        this.code = code;
//        this.message = message;
//        this.stackTrace = stackTrace;
//    }

//    public int getCode() {
//        return code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public String getStackTrace() {
//        return stackTrace;
//    }
}
