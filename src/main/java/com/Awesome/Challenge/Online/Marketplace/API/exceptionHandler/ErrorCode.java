package com.finalyear.VolunteeringSystm.exceptionHandler;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "Resource already exists"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Validation Failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
//public enum ErrorCode {
//    VALIDATION_ERROR(400, "Validation Failed"),
//    UNAUTHORIZED(401, "Unauthorized"),
//    FORBIDDEN(403, "Forbidden"),
//    NOT_FOUND(404, "Not Found"),
//    CONFLICT(409, "Conflict"),
//    SERVER_ERROR(500, "Server Error");
//
//    private final int code;
//    private final String message;
//
//    ErrorCode(int code, String message) {
//        this.code = code;
//        this.message = message;
//    }
//
//    public int getCode() {
//        return code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//}