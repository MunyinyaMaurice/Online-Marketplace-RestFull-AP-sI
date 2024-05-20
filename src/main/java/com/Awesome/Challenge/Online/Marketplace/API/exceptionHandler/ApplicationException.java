package com.finalyear.VolunteeringSystm.exceptionHandler;

public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode ;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}